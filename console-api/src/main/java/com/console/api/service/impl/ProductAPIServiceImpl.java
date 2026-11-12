package com.console.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.console.api.dto.req.ProductReq;
import com.console.api.dto.res.ProductOrderPageRes;
import com.console.api.service.ProductAPIService;
import com.console.core.entity.Parameter;
import com.console.core.entity.Tenant;
import com.console.core.entity.Wallet;
import com.console.core.entity.WalletProc;
import com.console.core.merger.WalletProcMerger;
import com.console.core.model.bo.MarketSiteBo;
import com.console.core.service.WalletService;
import com.console.framework.constants.CacheConstants;
import com.console.framework.constants.ParameterConstants;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.Result;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.*;
import com.console.product.api.TransferApi;
import com.console.product.api.res.TransferRecordRes;
import com.console.product.entity.*;
import com.console.product.service.*;
import com.console.user.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Service
public class ProductAPIServiceImpl extends BaseServiceImpl implements ProductAPIService {
    @Value("${console.serviceCode}")
    private String serviceCode;
    @Value("${console.transfer.url}")
    private String transferUrl;
    @Value("${console.transfer.apiKey}")
    private String apiKey;
    @Value("${console.transfer.secret}")
    private String secret;

    @Resource
    private WalletService walletService;
    @Resource
    private ProductService productService;
    @Resource
    private UserProductService userProductService;
    @Resource
    private ProductPlatformService productPlatformService;
    @Resource
    private RedisLockRegistry lockRegistry;
    @Resource
    private TransferApi transferApi;
    @Resource
    private OrderModeService orderModeService;
    @Resource
    private PlatformBelongRuleService platformBelongRuleService;
    @Resource
    private ProductOrderService productOrderService;
    @Resource
    private PlatformBelongRuleItemService platformBelongRuleItemService;
    @Resource
    private WalletProcMerger walletProcMerger;

    @Override
    public String enterProduct(ProductReq.EnterProductReq enterProductReq) {
        RequestMember member = RequestUtils.getRequestMember();
        MarketSiteBo marketSite = marketSiteService.getMarketSiteByDomain(RequestUtils.getRequestDomain());
        if (marketSite.getTenantStatus() != Tenant.TenantStatus.OPEN) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("tenant.error.status.stop"));
        }
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_PRODUCT_TRANSFER + member.getTenantId() + ":" + member.getId());
        boolean locked = false;
        Long transactionId = IdWorker.getId();
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (locked) {
                Product product = verifyProduct(enterProductReq.getProductId());//产品信息校验
                verifyProductPlatform(product.getPlatformId());//产商信息校验
                Wallet wallet = verifyWallet(member.getId());//钱包信息校验
                //下单模式确认
                PlatformBelongRuleItem platformBelongRuleItem = verifyOrderMode(member,wallet,product);
                //进入中转平台
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tenantId", TenantUtils.getTenantId());
                jsonObject.put("userId", wallet.getUserId());
                jsonObject.put("gameId",product.getId());
                jsonObject.put("amount",wallet.getBalance());
                jsonObject.put("tranId", transactionId);
                jsonObject.put("locale",RequestUtils.getLang());
                jsonObject.put("merchantCode",platformBelongRuleItem.getMerchantCode());
                if (StringUtils.hasText(platformBelongRuleItem.getSubMerchantCode())) jsonObject.put("subMerchantCode",platformBelongRuleItem.getSubMerchantCode());
                jsonObject.put("secreley",platformBelongRuleItem.getSecretkey());
                jsonObject.put("ipAddress",RequestUtils.getRequestIp());
                jsonObject.put("memberSuffix",platformBelongRuleItem.getUserSuffix());

//                TransferReq.EnterTransferReq enterTransferReq = new TransferReq.EnterTransferReq(member,platformBelongRuleItem);
//                enterTransferReq.setGameId(product.getId());
//                enterTransferReq.setAmount(wallet.getBalance());
//                enterTransferReq.setTranId(transactionId.toString());



                String timestamp = DateUtils.getCurrentTimestampOfMs().toString();
                String sign = EncryptUtils.hmacSHA256Encrypt(jsonObject.toString(),secret,apiKey + timestamp + serviceCode);
                Result result = transferApi.enterProduct(transferUrl + "/api/game/prepare",apiKey, timestamp,sign,jsonObject).execute().body();
                if (result != null) {
                    if (result.getCode() == 1001) {//中转服务转账失败，无需扣款
                        log.error("进入中转平台失败，结果报文：{}",JSON.toJSONString(result));
                        throw new BusinessException(500,I18nUtil.getI8nMsg("product.enter.error.busyWithBusiness"));
                    }
                    UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.lambda().set(Wallet::getBalance, BigDecimal.ZERO).set(Wallet::getAmtLocation,1)
                            .eq(Wallet::getId, wallet.getId());
                    if (wallet.isSwitchOrderModelIndex()) {
                        updateWrapper.lambda().set(Wallet::getOrderModeIndex,wallet.getOrderModeIndex()).set(Wallet::getSwitchIndexTime, LocalDateTime.now());
                    }
                    walletService.update(updateWrapper);//钱包更新成功
                    member.setTransferStatus(1);
                    TokenUtils.updateUserToken(member);
                    //添加钱包过程
                    WalletProc walletProc = new WalletProc(transactionId,wallet.getUserId(),wallet.getWalletType(),1, WalletProc.ProcType.TRANSFER_TO_THIRD_PARTY,BigDecimal.ZERO,wallet.getBalance(),null);
                    walletProcMerger.submit(walletProc);
                    if (result.getCode() == 0) {
                        userProductService.addUserProduct(product.getId(),member.getTenantId(),member.getId(),UserProduct.Type.HISTORY);
                        return result.getData().toString();
                    }
                    throw new BusinessException(500,result.getMsg());
                }
                throw new BusinessException(500,I18nUtil.getI8nMsg("product.enter.error.busyWithBusiness"));
            }
            throw new BusinessException(500,I18nUtil.getI8nMsg("product.enter.error.transferIng"));
        } catch (IOException e) {
            log.error("进入产品中转请求异常：", e);
            UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(Wallet::getTransferStatus, 2)
                    .set(Wallet::getTransactionId, transactionId)
                    .eq(Wallet::getUserId,member.getId())
                    .eq(Wallet::getWalletType,Wallet.WalletType.USER_WALLET);
            walletService.update(updateWrapper);
            throw new BusinessException(500,e.getMessage());
        } catch (InterruptedException e) {//获取分布式锁失败
            throw new BusinessException(500,I18nUtil.getI8nMsg("product.enter.error.transferIng"));
        } finally {
            if (locked) lock.unlock();
        }
    }

    @Override
    public Wallet logoutTransfer() {
        RequestMember member = RequestUtils.getRequestMember();
        Lock lock = lockRegistry.obtain(CacheConstants.ACQUIRE_LOCK_PRODUCT_TRANSFER + member.getTenantId() + ":" + member.getId());
        boolean locked = false;
        Long transactionId = IdWorker.getId();
        try {
            locked = lock.tryLock(100, TimeUnit.MILLISECONDS);
            if (locked) {
                Wallet wallet = verifyWallet(member.getId());
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tenantId", TenantUtils.getTenantId());
                jsonObject.put("userId", member.getId());
                jsonObject.put("tranId", transactionId);
                String timestamp = DateUtils.getCurrentTimestampOfMs().toString();
                String sign = EncryptUtils.hmacSHA256Encrypt(jsonObject.toString(),secret,apiKey + timestamp + serviceCode);
                Result result = transferApi.logoutTransfer(transferUrl + "/api/game/logout",apiKey, timestamp,sign,jsonObject).execute().body();
                if (result != null) {
                    if (result.getCode() == 1002) {//中转平台转出失败
                        return wallet;
                    }
                    if (result.getCode() == 0) {//退出中转平台成功
                        UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.lambda().eq(Wallet::getId, wallet.getId()).set(Wallet::getAmtLocation,0);
                        member.setTransferStatus(0);
                        TokenUtils.updateUserToken(member);
                        TransferRecordRes transferRecordRes = JSON.parseObject(JSON.toJSONString(result.getData()), TransferRecordRes.class);
                        if (transferRecordRes.getBeforeAmount().compareTo(BigDecimal.ZERO) > 0 ||
                            transferRecordRes.getAmount().compareTo(BigDecimal.ZERO) > 0) {//转入金额大于零 或 返还金额大于零则添加转出记录
                            BigDecimal balanceAmt = wallet.getBalance().add(transferRecordRes.getAmount());//第三方转回后的账户金额
                            updateWrapper.setSql("balance = balance + " + transferRecordRes.getAmount());
                            if (wallet.getToBeOrderAmt().compareTo(BigDecimal.ZERO) == 0 || balanceAmt.compareTo(wallet.getCashAbleAmt()) < 0) {
                                //下单量为零或者用户钱包余额小于可提现余额则直接将用户余额赋值给可提现余额
                                updateWrapper.setSql("cash_able_amt = balance");
                            }
                            walletService.update(updateWrapper);//更新钱包信息
                            wallet.setBalance(balanceAmt);
                            WalletProc walletProc = new WalletProc(wallet.getTransactionId(),wallet.getUserId(),wallet.getWalletType(),1,WalletProc.ProcType.TRANSFER_FROM_THIRD_PARTY,
                                    wallet.getBalance(),transferRecordRes.getAmount(),null);
                            walletProcMerger.submit(walletProc);//添加退出中转平台钱包记录
                        } else {
                            walletService.update(updateWrapper);//更新钱包信息
                        }
                    }
                }
                return wallet;
            }
        } catch (IOException e) {
            //IO异常标记异常记录，下次和中转平台交互必须先处理异常
            UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(Wallet::getTransactionId, transactionId)
                    .set(Wallet::getTransferStatus, 2)
                    .eq(Wallet::getUserId, member.getId())
                    .eq(Wallet::getWalletType, Wallet.WalletType.USER_WALLET);
            walletService.update(updateWrapper);
            throw new BusinessException(500,e.getMessage());
        } catch (InterruptedException e) {
            throw new BusinessException(500,I18nUtil.getI8nMsg("product.enter.error.transferIng"));
        } finally {
            if (locked) lock.unlock();
        }
        return null;
    }

    @Override
    public List<Integer> getCollectProductIds() {
        return userProductService.getCollectProductIdList();
    }

    /** 收藏产品 **/
    @Override
    public void collectProduct(Integer productId) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        userProductService.addUserProduct(productId,requestMember.getTenantId(),requestMember.getId(), UserProduct.Type.COLLECT);
    }

    /** 取消收藏产品 **/
    @Override
    public void cancelCollectProduct(Integer productId) {
        userProductService.removeUserProduct(productId,UserProduct.Type.COLLECT);
    }

    @Override
    public List<Integer> getHistoryProductIds() {
        return userProductService.getHistoryProductIdList();
    }

    @Override
    public ProductOrderPageRes getProductOrderRecord(ProductReq.SearchProductOrderReq searchProductOrderReq) {
        Page<ProductOrder> page = new Page<>(searchProductOrderReq.getCurrent(), searchProductOrderReq.getSize());
        RequestMember requestMember = RequestUtils.getRequestMember();
        QueryWrapper<ProductOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,order_time,product_id,order_amt,result_amt,result_status").lambda()
                .eq(ProductOrder::getUserId,requestMember.getId())
                .between(searchProductOrderReq.getStartSearchTime() != null && searchProductOrderReq.getEndSearchTime() != null,ProductOrder::getOrderTime,searchProductOrderReq.getStartSearchTime(),searchProductOrderReq.getEndSearchTime())
                .eq(searchProductOrderReq.getProductId() != null,ProductOrder::getProductId,searchProductOrderReq.getProductId())
                .eq(searchProductOrderReq.getCategory()  != null,ProductOrder::getCategory,searchProductOrderReq.getCategory())
                .orderByDesc(ProductOrder::getId);
        IPage<ProductOrder> productOrderIPage = productOrderService.page(page,queryWrapper);
//        if (searchProductOrderReq.getCurrent() == 1 && productOrderIPage.getTotal() > 0) {
//
//        }
        if (productOrderIPage.getRecords() != null && !productOrderIPage.getRecords().isEmpty()) {
            Map<Integer,String> productNameMap = new HashMap<>();
            List<Integer> productIds = productOrderIPage.getRecords().stream().map(ProductOrder::getProductId).toList();
            for (Integer productId : productIds) {
                Product product = productService.getProductById(productId);
                if (product != null) {
                    productNameMap.put(productId,product.getProductName());
                }
            }
            for (ProductOrder productOrder : productOrderIPage.getRecords()) {
                productOrder.setProductName(productNameMap.get(productOrder.getProductId()));
            }
        }
        return new ProductOrderPageRes(productOrderIPage);
    }

    /**校验产品信息**/
    private Product verifyProduct(Integer productId) {
        Product product = productService.getProductById(productId);
        if (product == null || product.getStatus() == 0) {//产品不存在或已下架
            throw new BusinessException(500, I18nUtil.getI8nMsg("product.enter.error.status.stop"));
        }
        return product;
    }

    /**校验产商信息**/
    private void verifyProductPlatform(Integer platformId) {
        ProductPlatform productPlatform = productPlatformService.getProductPlatformById(platformId);
        if (productPlatform == null || productPlatform.getStatus() == 0) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("productPlatform.error.status.stop"));
        }
    }

    /**校验钱包信息**/
    private Wallet verifyWallet(Integer userId) {
        QueryWrapper<Wallet> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id,user_id,balance,to_be_order_amt,cash_able_amt,amt_location,order_mode,order_mode_index,switch_index_time," +
                        "transfer_status,transaction_id").lambda()
                .eq(Wallet::getUserId,userId)
                .eq(Wallet::getWalletType,Wallet.WalletType.USER_WALLET);
        Wallet wallet = walletService.getOne(queryWrapper);
        wallet.setSwitchOrderModelIndex(false);
        if (wallet.getTransferStatus() != null && wallet.getTransferStatus() == 2) {//与中转平台存在转账记录异常，先解决异常
            fixAbnormalWalletStatus(wallet);
        }
        return wallet;
    }


    /** 修复异常钱包状态 **/
    private void fixAbnormalWalletStatus(Wallet wallet) {
        UpdateWrapper<Wallet> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().eq(Wallet::getId,wallet.getId()).set(Wallet::getTransferStatus,1);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tenantId", TenantUtils.getTenantId());
            jsonObject.put("userId", wallet.getUserId());
            jsonObject.put("tranId", wallet.getTransactionId());
            log.error("\n================开始查询异常转账订单================\n查询异常请求id：" + wallet.getTransactionId());
            String timestamp = DateUtils.getCurrentTimestampOfMs().toString();
            String sign = EncryptUtils.hmacSHA256Encrypt(jsonObject.toString(),secret,apiKey + timestamp + serviceCode);
            Result result = transferApi.queryTransferRecord(transferUrl + "/api/getTenantTransferRecord",apiKey, timestamp,sign,jsonObject).execute().body();
            if (result != null && result.getCode() == 0) {//请求成功
                WalletProc walletProc;
                TransferRecordRes transferRecordRes = JSON.parseObject(JSON.toJSONString(result.getData()), TransferRecordRes.class);
                if (transferRecordRes.getTranType() == 1) {//该记录为转入中转平台记录
                    wallet.setBalance(wallet.getBalance().subtract(transferRecordRes.getAmount()));
                    updateWrapper.setSql("balance = GREATEST(balance - " + transferRecordRes.getAmount() + ",0)")
                            .lambda().set(Wallet::getAmtLocation,1);
                    walletProc = new WalletProc(wallet.getTransactionId(),wallet.getUserId(),wallet.getWalletType(),2,WalletProc.ProcType.TRANSFER_TO_THIRD_PARTY,
                            wallet.getBalance(),transferRecordRes.getAmount(),null);
                } else {//转出记录
                    wallet.setBalance(wallet.getBalance().subtract(transferRecordRes.getAmount()));
                    updateWrapper.setSql("balance = balance + " + transferRecordRes.getAmount()).lambda().set(Wallet::getAmtLocation,0);
                    walletProc = new WalletProc(wallet.getTransactionId(),wallet.getUserId(),wallet.getWalletType(),1,WalletProc.ProcType.TRANSFER_FROM_THIRD_PARTY,
                            wallet.getBalance(),transferRecordRes.getAmount(),null);
                }
                walletService.update(updateWrapper);
                wallet.setTransferStatus(1);
                walletProcMerger.submit(walletProc);
            } else if (result != null && result.getCode() == 404) {//该记录不存在，无需变更钱包金额，清除钱包异常状态即可
                walletService.update(updateWrapper);
            }
        } catch (Exception e) {
            log.info("查询与中转平台异常转账记录结果出错，错误信息：" , e);
            throw new RuntimeException(e);
        }
    }

    /**校验产品订单模式**/
    private PlatformBelongRuleItem verifyOrderMode(RequestMember requestMember,Wallet wallet,Product product) {
        PlatformBelongRule platformBelongRule = null;
        if (wallet.getWalletType() == Wallet.WalletType.TRIAL_WALLET) {//资金来源 1、测试户；0、正式户
            OrderMode orderMode = orderModeService.getSysOrderModeByType(requestMember.getSiteId(),OrderMode.OrderModeType.TRIAL.getKey());
            platformBelongRule = platformBelongRuleService.getPlatformBelongRuleById(orderMode.getSwitchRules().get(0).getPlatformBelongRuleId());
        } else {
            if (User.UserType.getUserType(requestMember.getUserType()) == User.UserType.TEST) {//测试用户直接使用测试库
                OrderMode orderMode = orderModeService.getSysOrderModeByType(0,OrderMode.OrderModeType.TEST.getKey());
                platformBelongRule = platformBelongRuleService.getPlatformBelongRuleById(orderMode.getSwitchRules().get(0).getPlatformBelongRuleId());
            } else {
                //是否开启时间段定制下单模式机制
                OrderMode orderMode = orderModeService.getSysOrderModeByType(requestMember.getSiteId(),wallet.getOrderMode());
                boolean openTimeSlotOrderMode = false;
                if (!List.of(OrderMode.OrderModeType.BLACKLIST.getKey(),OrderMode.OrderModeType.TEST.getKey()).contains(orderMode.getModeType())) {
                    Parameter timeSlotOrderModeParam = parameterService.getParameterByCode(requestMember.getTeamId(), ParameterConstants.P1032);
                    if (timeSlotOrderModeParam != null && timeSlotOrderModeParam.getIsEnable() == 1 && StringUtils.hasText(timeSlotOrderModeParam.getValue())) {//开启时间段下单模式
                        Parameter.TimeSlotOrderRuleParam timeSlotOrderRuleParam = JSON.parseObject(timeSlotOrderModeParam.getValue(), Parameter.TimeSlotOrderRuleParam.class);
                        if (DateUtils.isBetween(LocalTime.now(), timeSlotOrderRuleParam.getStartTime(), timeSlotOrderRuleParam.getEndTime())) {
                            openTimeSlotOrderMode = true;
                            platformBelongRule = platformBelongRuleService.getPlatformBelongRuleById(timeSlotOrderRuleParam.getRuleId());
                        }
                    }
                }
                if (!openTimeSlotOrderMode) {//未开启时间段下单机制，按正常计算当前下单商户
                    if (orderMode.getSwitchRules().size() == 1) {//只有一个规则，无需切换
                        platformBelongRule = platformBelongRuleService.getPlatformBelongRuleById(orderMode.getSwitchRules().get(0).getPlatformBelongRuleId());
                    } else {
                        int index = wallet.getOrderModeIndex() > orderMode.getSwitchRules().size() ? orderMode.getSwitchRules().size() : wallet.getOrderModeIndex();
                        Integer ruleId = orderMode.getSwitchRules().get(index - 1).getPlatformBelongRuleId();
                        long switchThreshold = orderMode.getSwitchRules().get(index - 1).getSwitchRule();//切换产商阈值
                        platformBelongRule = platformBelongRuleService.getPlatformBelongRuleById(ruleId);
                        if (platformBelongRule.getUserSuffix() == null) {
                            platformBelongRule.setUserSuffix(0);
                        }
                        QueryWrapper<ProductOrder> productOrderQueryWrapper = new QueryWrapper<>();
                        productOrderQueryWrapper.lambda()
                                .eq(ProductOrder::getUserId, wallet.getId())
                                .ge(wallet.getSwitchIndexTime() != null, ProductOrder::getOrderTime, wallet.getSwitchIndexTime())
                                .eq(ProductOrder::getUserSuffix, platformBelongRule.getUserSuffix());
                        long orderedTimes = productOrderService.count(productOrderQueryWrapper);
                        log.info("当前模式已下单：{}次，转变模式需要下单{}次", orderedTimes, switchThreshold);
                        if (orderedTimes >= switchThreshold) {//已达到切换商户类型下次次数阈值
                            if (wallet.getOrderModeIndex() >= orderMode.getSwitchRules().size()) {
                                index = 1;
                            } else {
                                index = wallet.getOrderModeIndex() + 1;
                            }
                            wallet.setSwitchOrderModelIndex(true);
                            wallet.setOrderModeIndex(index);
                            ruleId = orderMode.getSwitchRules().get(index - 1).getPlatformBelongRuleId();
                            platformBelongRule = platformBelongRuleService.getPlatformBelongRuleById(ruleId);
                        }
                    }
                }
            }
        }
        PlatformBelongRuleItem ruleItem = platformBelongRuleItemService.getPlatformBelongRuleItem(platformBelongRule.getId(),product.getPlatformId());
        ruleItem.setUserSuffix(platformBelongRule.getUserSuffix());
        return ruleItem;
    }
}
