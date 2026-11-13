package com.console.api.dto.res;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.console.core.model.res.IPageRes;

public class SubPerformancePageRes extends IPageRes<SubPerformanceReport> {
    public SubPerformancePageRes(IPage<SubPerformanceReport> subPerformanceReportIPage) {
        this.setSize(subPerformanceReportIPage.getSize());
        this.setCurrent(subPerformanceReportIPage.getCurrent());
        this.setTotal(subPerformanceReportIPage.getTotal());
        this.setPages(subPerformanceReportIPage.getPages());
        this.setRecords(subPerformanceReportIPage.getRecords());
    }
}
