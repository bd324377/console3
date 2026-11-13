package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.model.res.IPageRes;
import com.console.user.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubUserPageRes extends IPageRes<SubUserRes> {
    SubTeamRechargeReport subTeamRechargeReport;

    public SubUserPageRes(IPage<SubUserRes> userIPage) {
        this.setSize(userIPage.getSize());
        this.setCurrent(userIPage.getCurrent());
        this.setTotal(userIPage.getTotal());
        this.setPages(userIPage.getPages());
        this.setRecords(userIPage.getRecords());
    }
}
