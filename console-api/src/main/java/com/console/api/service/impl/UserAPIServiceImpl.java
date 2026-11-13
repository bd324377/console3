package com.console.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.console.api.dto.req.UserReq;
import com.console.api.dto.res.SelfInfoRes;
import com.console.api.service.UserAPIService;
import com.console.core.entity.UserProc;
import com.console.core.merger.UserProcMerger;
import com.console.framework.domain.BusinessException;
import com.console.framework.domain.ResultCode;
import com.console.framework.request.RequestMember;
import com.console.framework.utils.EncryptUtils;
import com.console.framework.utils.I18nUtil;
import com.console.framework.utils.RequestUtils;
import com.console.framework.utils.TokenUtils;
import com.console.user.entity.Avatar;
import com.console.user.entity.BindPhoneRecord;
import com.console.user.entity.User;
import com.console.user.mapper.BindPhoneRecordMapper;
import com.console.user.model.bo.VipBo;
import com.console.user.service.AvatarService;
import com.console.user.service.UserService;
import com.console.user.service.VipService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserAPIServiceImpl implements UserAPIService {
    @Resource
    private UserService userService;
    @Resource
    private VipService vipService;
    @Resource
    private UserProcMerger userProcMerger;
    @Resource
    private BindPhoneRecordMapper bindPhoneRecordMapper;
    @Resource
    private AvatarService avatarService;

    @Override
    public SelfInfoRes selfInfo() {
        RequestMember requestMember = RequestUtils.getRequestMember();
        if (requestMember == null) {
            if (StringUtils.hasText(RequestUtils.getTokenKey())) {
                log.info("查询用户信息未失败，当前用户未登录，当前请求的token信息为：{}", RequestUtils.getTokenKey());
            }
            throw new BusinessException(ResultCode.AUTHENTICATION_FAILED);
        }
        SelfInfoRes selfInfoRes = new SelfInfoRes();
        selfInfoRes.setAccount(requestMember.getAccount());
        selfInfoRes.setUserId(requestMember.getId());
        selfInfoRes.setAvatar(requestMember.getAvatar());

        VipBo vip = vipService.getVipById(requestMember.getVipId());
        selfInfoRes.setVipLevel(vip.getLevel());
        selfInfoRes.setVipName(vip.getVipName());
        selfInfoRes.setVipIcon(vip.getIcon());
        selfInfoRes.setUpgradeRechargeAmt(vip.getUpgradeRechargeAmt());
        selfInfoRes.setUpgradeOrderAmt(vip.getUpgradeOrderAmt());
        selfInfoRes.setOrderAmt(requestMember.getOrderAmt());
        selfInfoRes.setRechargeAmt(requestMember.getRechargeAmt());

        selfInfoRes.setPhone(requestMember.getPhone());
        selfInfoRes.setPhoneStatus(requestMember.getPhoneStatus());
        selfInfoRes.setShareCode(EncryptUtils.encodeToBase64Str("1_" + requestMember.getId()));
        selfInfoRes.setHasCashOutPwd(StringUtils.hasText(requestMember.getCashOutPwd()));

        return selfInfoRes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindPhone(UserReq.BindPhoneReq bindPhoneReq) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        if (requestMember.getPhoneStatus() == 0) {//未绑定则执行绑定动作，已绑定则返回已绑定提醒
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(User::getPhone,bindPhoneReq.getPhone())
                    .set(User::getPhoneStatus,1).eq(User::getId,requestMember.getId());
            try {
                if (userService.update(updateWrapper)) {//更新用户手机号和绑定转态
                    //更新成功后续添加用户绑定手机记录，修改Token中缓存信息
                    BindPhoneRecord bindPhoneRecord = new BindPhoneRecord();
                    bindPhoneRecord.setPhone(bindPhoneReq.getPhone());
                    bindPhoneRecord.setBindTime(LocalDateTime.now());
                    bindPhoneRecord.setUserId(requestMember.getId());
                    bindPhoneRecord.setMarketSiteId(requestMember.getSiteId());
                    bindPhoneRecordMapper.insert(bindPhoneRecord);
                    UserProc bindPhoneProc = new UserProc(requestMember.getId(), requestMember.getTenantId(), UserProc.ProcType.BIND_PHONE);
                    userProcMerger.submit(bindPhoneProc);
                    requestMember.setPhone(bindPhoneReq.getPhone());
                    requestMember.setPhoneStatus(1);
                    TokenUtils.updateUserToken(requestMember);

                }
            } catch (DuplicateKeyException e) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("该号码已被他人绑定"));
            }
        }
        throw new BusinessException(500, I18nUtil.getI8nMsg("user.error.bindPhone.alreadyBind"));//绑定手机号失败，用户已绑定手机号
    }

    @Override
    public boolean bindEmail(UserReq.BindEmailReq bindEmailReq) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        if (requestMember.getPhoneStatus() == 0) {//未绑定则执行绑定动作，已绑定则返回已绑定提醒
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .set(User::getEmail,bindEmailReq.getEmail())
                    .set(User::getEmailStatus,1).eq(User::getId,requestMember.getId());
            try {
                if (userService.update(updateWrapper)) {//更新用户邮箱和绑定转态
                    //更新成功后续添加用户绑定邮箱记录，修改Token中缓存信息
                    UserProc bindPhoneProc = new UserProc(requestMember.getId(), requestMember.getTenantId(), UserProc.ProcType.BIND_EMAIL);
                    userProcMerger.submit(bindPhoneProc);
                    requestMember.setEmail(bindEmailReq.getEmail());
                    requestMember.setEmailStatus(1);
                    TokenUtils.updateUserToken(requestMember);

                }
            } catch (DuplicateKeyException e) {
                throw new BusinessException(500,I18nUtil.getI8nMsg("该号码已被他人绑定"));
            }
        }
        throw new BusinessException(500, I18nUtil.getI8nMsg("user.error.bindPhone.alreadyBind"));//绑定手机号失败，用户已绑定手机号
    }

    @Override
    public boolean updateLoginPwd(UserReq.UpdatePwdReq updatePwdReq) {
        if (!updatePwdReq.getNewPassword().equals(updatePwdReq.getAgainPassword())) {
            throw new BusinessException(500,"修改密码失败，再次输入密码不一致");
        }
        RequestMember requestMember = RequestUtils.getUser(RequestMember.class);
        if (!requestMember.getLoginPwd().equals(updatePwdReq.getPassword())) {
            throw new BusinessException(500,"修改密码失败，旧密码输入错误");
        }
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(User::getLoginPwd,updatePwdReq.getNewPassword()).eq(User::getId,requestMember.getId());
        if (userService.update(updateWrapper)) {
            UserProc bindPhoneProc = new UserProc(requestMember.getId(), requestMember.getTenantId(), UserProc.ProcType.UPDATE_LOGIN_PWD);
            userProcMerger.submit(bindPhoneProc);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateSelfInfo(UserReq.UpdateUserInfoReq updateUserInfoReq) {
        if (updateUserInfoReq.isUpdate()) {
            RequestMember requestMember = RequestUtils.getRequestMember();
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda().eq(User::getId,requestMember.getId());
            if (StringUtils.hasText(updateUserInfoReq.getNickName())) {
                updateWrapper.lambda().set(User::getNickName, updateUserInfoReq.getNickName());
                requestMember.setNickName(updateUserInfoReq.getNickName());
            }
            if (StringUtils.hasText(updateUserInfoReq.getAvatar())) {
                updateWrapper.lambda().set(User::getAvatar, updateUserInfoReq.getAvatar());
                requestMember.setAvatar(updateUserInfoReq.getAvatar());
            }
            if (userService.update(updateWrapper)) {//修改成功，更新缓存信息
                TokenUtils.updateUserToken(requestMember);
                return true;
            }
        }
        return true;
    }

    @Override
    public List<Avatar> getAvatarList(Integer sex) {
        RequestMember requestMember = RequestUtils.getRequestMember();
        return avatarService.getAvatarList(requestMember.getMarketerId(),sex);
    }

}
