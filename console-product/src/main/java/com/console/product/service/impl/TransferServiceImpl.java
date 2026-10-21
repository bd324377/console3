package com.console.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.core.entity.Tenant;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.config.DynamicTableNameHandler;
import com.console.framework.domain.Result;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.EncryptUtils;
import com.console.product.api.TransferApi;
import com.console.product.entity.Product;
import com.console.product.entity.TempProductOrder;
import com.console.product.entity.Transfer;
import com.console.product.mapper.*;
import com.console.product.service.TransferService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransferServiceImpl extends BaseServiceImpl<TransferMapper,Transfer> implements TransferService {
    @Value("${console.serviceCode}")
    private String serviceCode;
    @Value("${console.transfer.url}")
    private String transferUrl;
    @Value("${console.transfer.apiKey}")
    private String apiKey;
    @Value("${console.transfer.secret}")
    private String secret;
    @Resource
    private TransferApi transferApi;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private TempProductOrderMapper tempProductOrderMapper;

    public void forceRefreshProductData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("table", "product");
        jsonObject.put("limit", 5000);
        jsonObject.put("startId", 0);
        try {
            String timestamp = DateUtils.getCurrentTimestampOfMs().toString();
            String sign = EncryptUtils.hmacSHA256Encrypt(jsonObject.toString(),secret,apiKey + timestamp + serviceCode);
            Result result = transferApi.syncTransferData(transferUrl + "/api/serverData/pull",apiKey, timestamp,sign,jsonObject).execute().body();
            if (result != null && result.getCode() == 0) {
                if (!ObjectUtils.isEmpty(result.getData())) {
                    List<Product> productList = JSON.parseArray(JSON.toJSONString(result.getData()), Product.class);
                    log.info("强制更新");
                    productMapper.batchInsertOrUpdate(productList);
                    Product lastProduct = productList.get(productList.size() - 1);
                    UpdateWrapper<Transfer> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().set(Transfer::getProductMaxId,lastProduct.getId()).eq(Transfer::getId,1);
                    this.update(updateWrapper);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void fetchProductData() {
        QueryWrapper<Transfer> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("product_max_id");
        Transfer transfer = this.getOne(queryWrapper);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("table", "product");
        jsonObject.put("limit", 5000);
        jsonObject.put("startId", transfer != null ? transfer.getProductMaxId() : 0);
        try {
            String timestamp = DateUtils.getCurrentTimestampOfMs().toString();
            String sign = EncryptUtils.hmacSHA256Encrypt(jsonObject,secret,apiKey + timestamp + serviceCode);
            log.info("加密结果：{}",sign);
            Result result = transferApi.syncTransferData(transferUrl + "/api/serverData/pull",apiKey,timestamp,sign,jsonObject).execute().body();
            if (result != null && result.getCode() == 0) {
                if (!ObjectUtils.isEmpty(result.getData())) {
                    List<Product> productList = JSON.parseArray(JSON.toJSONString(result.getData()), Product.class);
                    productMapper.batchInsertOrUpdate(productList);
                    Product lastProduct = productList.get(productList.size() - 1);
                    UpdateWrapper<Transfer> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().eq(Transfer::getId,1).set(Transfer::getProductMaxId,lastProduct.getId());
                    this.update(updateWrapper);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void fetchProductOrderData() {
        List<TempProductOrder> orderList = new ArrayList<>();
        while (true) {
            Long startId = null;
            if (!orderList.isEmpty()) {
                startId = orderList.get(orderList.size() - 1).getId();
            } else {
                QueryWrapper<Transfer> queryWrapper = new QueryWrapper<>();
                queryWrapper.select("product_order_max_id").lambda().eq(Transfer::getId,1);
                Transfer transfer = this.getOne(queryWrapper);
                if (transfer != null) startId = transfer.getProductOrderMaxId();
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("table", "orders");
            jsonObject.put("limit", 5000);
            jsonObject.put("startId", startId != null ? startId : 0);
            try {
                String timestamp = DateUtils.getCurrentTimestampOfMs().toString();
                String sign = EncryptUtils.hmacSHA256Encrypt(jsonObject,secret,apiKey + timestamp + serviceCode);
                Result result = transferApi.syncTransferData(transferUrl + "/api/serverData/pull",apiKey,timestamp,sign,jsonObject).execute().body();
                if (result != null && result.getCode() == 0) {
                    if (!ObjectUtils.isEmpty(result.getData())) {
                        List<TempProductOrder> fetchDataList = JSON.parseArray(JSON.toJSONString(result.getData()), TempProductOrder.class);
                        if (ObjectUtils.isEmpty(fetchDataList)) {
                            break;
                        }
                        orderList.addAll(fetchDataList);
                        if (fetchDataList.size() < 5000) {
                            break;
                        }
                        if (orderList.size() > 50000) {
                            log.info("结束订单拉取，待拉取订单超过5W条，将分批拉取");
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                log.info("结束订单拉取，拉取订单失败");
                break;
            }
        }
        if (!orderList.isEmpty()) {
            List<Tenant> tenantList = tenantService.getTenantList();
            Map<Integer,List<TempProductOrder>> tenantProductOrderMap = orderList.stream().collect(Collectors.groupingBy(TempProductOrder::getTenantId));
            for (Integer tenantId : tenantProductOrderMap.keySet()) {
                Tenant tenant = tenantList.stream().filter(item -> item.getId().equals(tenantId)).findFirst().orElse(null);
                if (tenant == null) {
                    continue;
                }
                ZoneId targetZone = ZoneId.of(tenant.getTimeZone());
                DynamicTableNameHandler.setTenantId(tenantId);
                List<TempProductOrder> teanatOrderList = tenantProductOrderMap.get(tenantId);
                for (TempProductOrder productOrder : teanatOrderList) {
                    LocalDateTime targetLocal = productOrder.getOrderTime()
                            .atZone(ZoneOffset.UTC)          // 先视为 UTC 时间
                            .withZoneSameInstant(targetZone) // 同一时刻切换到目标时区
                            .toLocalDateTime();              // 去掉时区，得到目标时区的 LocalDateTime
                    productOrder.setOrderTime(targetLocal);
                    productOrder.setOrderDate(targetLocal.toLocalDate());
                    productOrder.setResultStatus(productOrder.getResultAmt().compareTo(BigDecimal.ZERO) > 0 ? 1 : (productOrder.getResultAmt().compareTo(BigDecimal.ZERO) < 0 ? 2 : 3));
                }
                tempProductOrderMapper.insert(teanatOrderList);
            }
            TempProductOrder lastOrder = orderList.get(orderList.size() - 1);
            UpdateWrapper<Transfer> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().set(Transfer::getProductOrderMaxId, lastOrder.getId()).eq(Transfer::getId, 1);
            this.update(updateWrapper);
        }
    }
}
