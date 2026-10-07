package com.console.user.model.bo;

import com.console.core.entity.Wallet;
import com.console.core.model.bo.MarketerBo;
import com.console.framework.utils.RequestUtils;
import com.console.user.entity.User;
import com.console.user.entity.UserRakeBackConfig;
import com.console.user.entity.UserShuntConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InviteNewConfig implements Serializable {//用户邀新配置信息

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;//用户ID
    private Integer tenantId;//租户ID
    private Integer marketSiteId;//站点ID
    private Integer marketTeamId;//团队（渠道）ID
    private Integer marketerId;//业务员Id
    private Integer subUserOrderMode;//下级用户下单模式
    private Map<Integer,UserShuntConfig> userShuntConfigs;//分流配置
    private Map<Integer,UserRakeBackConfig> userRakeBackConfigs;//返佣配置


    private Boolean shuntStatus;//分流状态
    private String shuntReason;//分流原因

    public InviteNewConfig() {}

    public InviteNewConfig(User user, Wallet wallet, List<UserShuntConfig> userShuntConfigs, List<UserRakeBackConfig> userRakeBackConfigs) {
        this.userId = user.getId();
        this.tenantId = user.getTenantId();
        this.marketSiteId = user.getMarketSiteId();
        this.marketTeamId = user.getMarketTeamId();
        this.marketerId = user.getMarketerId();
        this.subUserOrderMode = wallet.getSubUserOrderMode();
        if (!ObjectUtils.isEmpty(userShuntConfigs)) {
            this.userShuntConfigs = new HashMap<>();
            for (UserShuntConfig item : userShuntConfigs) {
                this.userShuntConfigs.put(item.getLayers(),item);
            }
        }
        if (!ObjectUtils.isEmpty(userRakeBackConfigs)) {
            this.userRakeBackConfigs = new HashMap<>();
            for (UserRakeBackConfig item : userRakeBackConfigs) {
                this.userRakeBackConfigs.put(item.getLayers(),item);
            }
        }
    }

    public InviteNewConfig(MarketerBo marketer) {
        this.tenantId = marketer.getTenantId();
        this.marketSiteId = marketer.getSiteId();
        this.marketTeamId = marketer.getMarketTeamId();
        this.marketerId = marketer.getMarketerId();
        this.subUserOrderMode = marketer.getMarketerOrderMode();
        if (marketer.getShuntStatus() != null && marketer.getShuntStatus() == 1) {
            this.userShuntConfigs = new HashMap<>();
            UserShuntConfig shuntConfig = new UserShuntConfig();
            shuntConfig.setShuntStatus(marketer.getShuntStatus());
            shuntConfig.setShuntRate(marketer.getShuntRate());
            shuntConfig.setShuntThreshold(marketer.getShuntThreshold());
            this.userShuntConfigs.put(1,shuntConfig);
        }
    }

    public void buildUserByConfig(User user) {
        user.setTenantId(this.tenantId);
        user.setMarketerId(this.marketSiteId);
        user.setMarketTeamId(this.marketTeamId);
        user.setMarketerId(this.marketerId);
        user.setRegIp(RequestUtils.getRequestIp());
        LocalDateTime dateTime = LocalDateTime.now();
        user.setRegTime(dateTime);
        user.setChgVipTime(dateTime);
        user.setChgAgentTime(dateTime);
        user.setLoginIp(user.getRegIp());
        user.setLoginTime(dateTime);
        if (this.getUserId() != null) {
            if (this.shuntStatus != null && this.shuntStatus) {
                user.setParentId(0);
                user.setRemarks(this.shuntReason);
            } else {
                user.setParentId(this.userId);
            }
            user.setRealParentId(this.userId);
        }
    }

    public List<UserShuntConfig> buildSubUserShuntConfig(Integer userId) {
        if (this.userShuntConfigs != null) {
            List<UserShuntConfig> userShuntConfigList = new ArrayList<>();
            for (Integer mapKey : this.userShuntConfigs.keySet()) {
                if (mapKey == 1) {
                    continue;
                }
                UserShuntConfig userShuntConfig = this.userShuntConfigs.get(mapKey);
                userShuntConfig.setUserId(userId);
                userShuntConfig.setLayers(userShuntConfig.getLayers() - 1);
                userShuntConfigList.add(this.userShuntConfigs.get(mapKey));
            }
            return userShuntConfigList;
        }
        return null;
    }

    public List<UserRakeBackConfig> buildSubUserRakeBackConfig(Integer userId) {
        if (this.userRakeBackConfigs != null) {
            List<UserRakeBackConfig> userRakeBackConfigList = new ArrayList<>();
            for (Integer mapKey : this.userRakeBackConfigs.keySet()) {
                if (mapKey == 1) {
                    continue;
                }
                UserRakeBackConfig userRakeBackConfig = this.userRakeBackConfigs.get(mapKey);
                userRakeBackConfig.setUserId(userId);
                userRakeBackConfig.setLayers(userRakeBackConfig.getLayers() - 1);
                userRakeBackConfigList.add(this.userRakeBackConfigs.get(mapKey));
            }
            return userRakeBackConfigList;
        }
        return null;
    }
}
