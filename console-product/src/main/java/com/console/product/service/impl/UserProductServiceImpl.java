package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.merger.UserProcMerger;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.RedisUtils;
import com.console.framework.utils.RequestUtils;
import com.console.product.entity.UserProduct;
import com.console.product.mapper.UserProductMapper;
import com.console.product.merger.UserProductMerger;
import com.console.product.service.UserProductService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserProductServiceImpl extends BaseServiceImpl<UserProductMapper, UserProduct> implements UserProductService {
    @Resource
    private UserProductMerger userProductMerger;
    @Override
    public void addUserProduct(Integer productId, Integer tenantId, Integer userId, UserProduct.Type type) {
        Integer timestamp = DateUtils.getCurrentTimestamp();
        String redisKey;int limitSize = 30;
        if (type == UserProduct.Type.COLLECT) {
            redisKey = CacheConstants.COOLECT_PRODUCT + tenantId + ":" + userId;
            limitSize = 1000;
        } else {
            redisKey = CacheConstants.HISTORY_PRODUCT + tenantId + ":" + userId;
        }
        if (!RedisUtils.existsInZSet(redisKey,productId)) {
            UserProduct userProduct = new UserProduct();
            userProduct.setProductId(productId);
            userProduct.setUserId(userId);
            userProduct.setType(type);
            userProduct.setUpdateTime(LocalDateTime.now());
            userProduct.setTenantId(tenantId);
            userProductMerger.submit(userProduct);
        }
        RedisUtils.addToZSet(redisKey, productId, timestamp,limitSize);
    }

    @Override
    public void removeUserProduct(Integer productId, UserProduct.Type type) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        String redisKey;
        if (type == UserProduct.Type.COLLECT) {
            redisKey = CacheConstants.COOLECT_PRODUCT + requestMember.getTenantId() + ":" + requestMember.getId();
        } else {
            redisKey = CacheConstants.HISTORY_PRODUCT + requestMember.getTenantId() + ":" + requestMember.getId();
        }
        if (RedisUtils.removeFromZSet(redisKey,productId)) {
            QueryWrapper<UserProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(UserProduct::getProductId,productId).eq(UserProduct::getUserId,requestMember.getId()).eq(UserProduct::getType,type);
            this.remove(queryWrapper);
        }
    }

    @Override
    public List<Integer> getHistoryProductIdList() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        String redisKey = CacheConstants.HISTORY_PRODUCT + requestMember.getTenantId() + ":" + requestMember.getId();
        List<Integer> historyProductIdList = RedisUtils.getZSetAdList(redisKey, Integer.class, 30);
        if (ObjectUtils.isEmpty(historyProductIdList)) {
            QueryWrapper<UserProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("product_id,update_time").lambda()
                    .eq(UserProduct::getUserId,requestMember.getId())
                    .eq(UserProduct::getType,UserProduct.Type.HISTORY)
                    .orderByDesc(UserProduct::getUpdateTime)
                    .last("LIMIT 30");
            return buildUserProductIds(queryWrapper,redisKey);
        }
        return historyProductIdList;
    }

    @Override
    public List<Integer> getCollectProductIdList() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        String redisKey = CacheConstants.COOLECT_PRODUCT + requestMember.getTenantId() + ":" + requestMember.getId();
        List<Integer> coolectProductIdList = new ArrayList<>();
        if (RedisUtils.exists(redisKey)) {
            coolectProductIdList = RedisUtils.getZSetAdList(redisKey, Integer.class, 1000);
        }
        if (ObjectUtils.isEmpty(coolectProductIdList)) {
            QueryWrapper<UserProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("product_id,update_time").lambda()
                    .eq(UserProduct::getUserId,requestMember.getId())
                    .eq(UserProduct::getType,UserProduct.Type.COLLECT)
                    .orderByDesc(UserProduct::getUpdateTime)
                    .last("LIMIT 1000");
            return buildUserProductIds(queryWrapper,redisKey);
        }
        return coolectProductIdList;
    }

    private List<Integer> buildUserProductIds(QueryWrapper<UserProduct> queryWrapper,String redisKey) {
        List<Integer> userProductIdList = new ArrayList<>();
        List<UserProduct> historyProductList = this.list(queryWrapper);
        if (!ObjectUtils.isEmpty(historyProductList)) {
            for (UserProduct userProduct : historyProductList) {
                userProductIdList.add(userProduct.getProductId());
                RedisUtils.addToZSet(redisKey,userProduct.getProductId(),DateUtils.getTimestampByLocalDateTime(userProduct.getUpdateTime()),30);
            }
        }
        return userProductIdList;
    }
}
