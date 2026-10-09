package com.console.payment.merger;

import com.console.core.merger.AbstractBatchMerger;
import com.console.core.merger.RecordType;
import com.console.payment.entity.PaymentProc;
import com.console.payment.mapper.PaymentProcMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PaymentProcMerger extends AbstractBatchMerger<PaymentProc> {
    @Resource
    private PaymentProcMapper paymentProcMapper;
    @Override
    protected int getRetryTimes(PaymentProc entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.PAYMENT_PROC;
    }

    @Override
    protected Class<PaymentProc> getRecordClass() {
        return PaymentProc.class;
    }

    @Override
    protected void saveBatchIgnore(List<PaymentProc> objectList) {
        paymentProcMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(PaymentProc entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(PaymentProc entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(PaymentProc entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected String getBusinessKey(PaymentProc entity) {
        return entity.getId().toString();
    }

    @Override
    protected Map<Integer, List<PaymentProc>> getGroupMap(List<PaymentProc> records) {
        return records.stream().collect(Collectors.groupingBy(PaymentProc::getTenantId));
    }
}
