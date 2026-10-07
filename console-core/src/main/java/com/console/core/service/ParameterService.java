package com.console.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.console.core.entity.Parameter;

public interface ParameterService extends IService<Parameter> {
    Parameter getParameterByCode(Integer marketTeamId,String parameterCode);
}
