package com.console.user.merger;
import com.console.core.merger.AbstractBatchMerger;
import com.console.core.merger.RecordType;
import com.console.user.entity.MarketSiteUser;
import com.console.user.mapper.MarketSiteUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MarketSiteUserMerger extends AbstractBatchMerger<MarketSiteUser> {
    @Resource
    private MarketSiteUserMapper marketSiteUserMapper;
    @Override
    protected int getRetryTimes(MarketSiteUser entity) {
        return entity.getRetryTimes() == null ? 0 : entity.getRetryTimes();
    }

    @Override
    protected RecordType getRecordType() {
        return RecordType.USER_ID;
    }

    @Override
    protected Class<MarketSiteUser> getRecordClass() {
        return MarketSiteUser.class;
    }

    @Override
    protected void saveBatchIgnore(List<MarketSiteUser> objectList) {
        marketSiteUserMapper.insert(objectList);
    }

    @Override
    protected void incrementRetryTimes(MarketSiteUser entity) {
        entity.setRetryTimes(entity.getRetryTimes() == null ? 0 : entity.getRetryTimes() + 1);
    }

    @Override
    protected boolean isFromRecovery(MarketSiteUser entity) {
        return entity.getFromRecovery() != null && entity.getFromRecovery();
    }

    @Override
    protected void setFromRecovery(MarketSiteUser entity, boolean fromRecovery) {
        entity.setFromRecovery(fromRecovery);
    }

    @Override
    protected Serializable getBusinessKey(MarketSiteUser entity) {
        return entity.getUserId();
    }

    @Override
    protected Map<Integer, List<MarketSiteUser>> getGroupMap(List<MarketSiteUser> records) {
        Map<Integer, List<MarketSiteUser>> groupMap = new HashMap<>();
        groupMap.put(0,records);
        return groupMap;
    }
}
