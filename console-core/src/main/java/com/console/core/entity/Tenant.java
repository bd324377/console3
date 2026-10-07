package com.console.core.entity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.*;
import com.console.framework.domain.BusinessException;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@TableName(value = "s_tenant")
public class Tenant implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    @TableId(type = IdType.AUTO)
    private Integer id;                      //租户ID
    private String tenantName;              //租户名称
    private Integer layoutId;                //默认布局配置ID
    private String domains;                 //前端绑定域名（JSON数组格式）
    private String backendDomains;          //后端绑定域名（JSON数组格式）
    private String timeZone;                //时区
    private String whiteIps;                //IP白名单列表（JSON数组格式）
    private String backendLimitIps;         //后端限制登录IP（JSON数组格式，空则不限制，有值则表示只有设定值能登录后台）
    private String pwaDownLoadDomain;       //PWA下载域名（JSON数组格式，随机跳转到对应域名）
    private String webPushConfig;           //推送配置（JSON格式）
    private String webVersion;              //前端版本号
    private String backendWebVersion;       //管理后台前端版本号
    private Integer orderMode;               //下单模型
    private BigDecimal manualDepositLimit;      //人工入款额度
    private BigDecimal manualDepositedAmt;      //已人工入款金额
    private BigDecimal manualWithdrawnAmt;      //人工出款金额
    private BigDecimal withdrawalLimit;         //出款额度，额度不足直接进入审核状态
    private BigDecimal rechargeRate;            //充值手续费百分比
    private BigDecimal rechargeAmt;             //充值金额
    private BigDecimal rechargeFee;             //充值手续费
    private BigDecimal withdrawAmt;             //出款金额
    private BigDecimal orderRate;               //订单手续费百分比
    private BigDecimal orderAmt;                //已下单金额
    private BigDecimal orderResultAmt;          //订单盈亏金额
    private BigDecimal orderFee;                //订单手续费金额
    private BigDecimal profitAmt;               //租户利润金额
    private TenantStatus status;                //租户状态：0-不对外开放（白名单人员才能进入），1-对外开放，2-暂停开放（可正常登录，但前端用户无法进入产品页面）
    private String     remark;                  //备注
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;           //创建时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;           //更新时间
    private LocalDateTime lastOpenTime;         //最近开放时间
    private LocalDateTime lastCloseTime;        //最近关闭时间

    /**
     * 租户校验（管理员）
     * @param tenant        域名绑定租户
     * @param targetTenant  目的租户
     */
    public static void verifyValidityOfAdmin(Tenant tenant, Tenant targetTenant) {
        if (tenant == null) {
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.tenant.notExist", RequestUtils.getLang()));
        }
        if (!tenant.getId().equals(targetTenant.getId()) && tenant.getId() != 0) {//非总租户域名只能登录当前域名租户
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.tenant.domainError",RequestUtils.getLang()));
        }
        if (!ObjectUtils.isEmpty(targetTenant.getBackendLimitIps()) && targetTenant.getBackendLimitIps().contains(RequestUtils.getRequestIp())) {//当前IP是否在白名单中
            throw new BusinessException(500, I18nUtil.getI8nMsg("backendUser.error.login.tenant.limitIp",RequestUtils.getLang()));
        }
    }

    public List<String> getDomains() {
        if (StringUtils.hasText(this.domains)) {
            return JSON.parseArray(this.domains,String.class);
        }
        return null;
    }
    public List<String> getBackendDomains() {
        if (StringUtils.hasText(this.backendDomains)) {
            return JSON.parseArray(this.backendDomains,String.class);
        }
        return null;
    }
    public List<String> getWhiteIps() {
        if (StringUtils.hasText(this.whiteIps)) {
            return JSON.parseArray(this.whiteIps,String.class);
        }
        return null;
    }
    @Getter
    public enum TenantStatus {
        CLOSED(0, "关闭"),
        OPEN(1, "开启"),
        MAINTENANCE(3, "维护中");

        @JsonValue
        @EnumValue
        private final Integer value;
        private final String desc;

        TenantStatus(int value, String desc) {
            this.value = value;
            this.desc = desc;
        }
    }
}
