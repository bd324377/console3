package com.console.product.merger;

import com.console.core.merger.AbstractBatchMerger;
import com.console.core.merger.RecordType;
import com.console.product.entity.UserProduct;
import com.console.product.mapper.UserProductMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserProductMerger extends AbstractBatchMerger<UserProduct> {
    @Resource
    private UserProductMapper userProductMapper;

    @Override
    protected int getRetryTimes(UserProduct entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.USER_PRODUCT;
    }

    @Override
    protected Class<UserProduct> getRecordClass() {
        return UserProduct.class;
    }

    @Override
    protected void saveBatchIgnore(List<UserProduct> objectList) {
        userProductMapper.replaceBatchUserProduct(objectList);
    }

    @Override
    protected void incrementRetryTimes(UserProduct entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(UserProduct entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(UserProduct entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected Serializable getBusinessKey(UserProduct entity) {
        return entity.getProductId();
    }

    @Override
    protected Map<Integer, List<UserProduct>> getGroupMap(List<UserProduct> records) {
        return records.stream().collect(Collectors.groupingBy(UserProduct::getTenantId));
    }
}
