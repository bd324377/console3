package com.console.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.Marketer;
import com.console.core.model.bo.MarketerBo;

import java.util.List;

public interface MarketerService extends IService<Marketer> {
    MarketerBo getMarketer(Integer marketerId);
    List<Marketer> getMarketerListBySiteId(Integer marketSiteId);
}
