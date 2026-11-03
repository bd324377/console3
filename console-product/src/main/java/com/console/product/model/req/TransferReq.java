package com.console.product.model.req;

import com.console.framework.request.RequestMember;
import com.console.framework.utils.RequestUtils;
import com.console.product.entity.PlatformBelongRuleItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

public class TransferReq {
    @Getter
    @Setter
    public static class EnterTransferReq {
        private String tenantId;
        private Integer userId;
        private Integer gameId;
        private BigDecimal amount;
        private String tranId;
        private String locale;
        private String merchantCode;
        private String subMerchantCode;
        private String secreley;
        private String ipAddress;
        private String memberSuffix;

        public EnterTransferReq() {}

        public EnterTransferReq(RequestMember member, PlatformBelongRuleItem platformBelongRuleItem) {
            this.setTenantId(member.getTenantId().toString());
            this.setUserId(member.getId());
            this.setLocale(RequestUtils.getLang());
            this.setMerchantCode(platformBelongRuleItem.getMerchantCode());
            this.setSubMerchantCode(platformBelongRuleItem.getSubMerchantCode());
            this.setSecreley(platformBelongRuleItem.getSecretkey());
            this.setIpAddress(RequestUtils.getRequestIp());
            this.setMemberSuffix(platformBelongRuleItem.getUserSuffix().toString());
        }
    }
}
