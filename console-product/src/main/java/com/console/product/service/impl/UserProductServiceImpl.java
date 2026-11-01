package com.console.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.console.core.service.impl.BaseServiceImpl;
import com.console.framework.constants.CacheConstants;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.DateUtils;
import com.console.framework.utils.RedisUtils;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TenantUtils;
import com.console.product.entity.UserProduct;
import com.console.product.mapper.UserProductMapper;
import com.console.product.service.UserProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserProductServiceImpl extends BaseServiceImpl<UserProductMapper, UserProduct> implements UserProductService {
    @Override
    public List<Long> getHistoryProductIdList() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        String redisKey = CacheConstants.HISTORY_PRODUCT + requestMember.getId() + ":" + requestMember.getId();
        List<Long> historyProductIdList = new ArrayList<>();
        if (RedisUtils.exists(redisKey)) {
            historyProductIdList = RedisUtils.getZSetAdList(redisKey, Long.class, 30);
        }
        if (ObjectUtils.isEmpty(historyProductIdList)) {
            QueryWrapper<UserProduct> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda()
                    .eq(UserProduct::getUserId,requestMember.getId())
                    .eq(UserProduct::getType,2)
                    .orderByDesc(UserProduct::getUpdateTime)
                    .last("LIMIT 30");
            List<UserProduct> historyProductList = this.list(queryWrapper);//this.baseMapper.selectHistoryProductIdList(userId);
            if (historyProductList != null && !ObjectUtils.isEmpty(historyProductList)) {
                for (UserProduct userProduct : historyProductList) {
                    historyProductIdList.add(userProduct.getProductId());
                    RedisUtils.addToZSet(redisKey,userProduct.getProductId(),DateUtils.getTimestampByLocalDateTime(userProduct.getUpdateTime()),30);
                }
            }
        }
        return historyProductIdList;
    }
}
