package com.console.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.core.entity.Parameter;
import com.console.core.mapper.ParameterMapper;
import com.console.core.service.ParameterService;
import com.console.framework.constants.CacheConstants;
import com.github.xiaolyuh.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ParameterServiceImpl extends ServiceImpl<ParameterMapper,Parameter> implements ParameterService {

    @Override
    @Cacheable( value = CacheConstants.PARAMETER, key = "T(com.console.framework.utils.TenantUtils).getTenantId() + ':' + #marketTeamId + ':' + #parameterCode")
    public Parameter getParameterByCode(Integer marketTeamId,String parameterCode) {
        QueryWrapper<Parameter> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(Parameter::getParameterCode, parameterCode).eq(Parameter::getMarketTeamId,marketTeamId);
        Parameter parameter = this.getOne(queryWrapper);
        if (parameter == null) {
            queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(Parameter::getParameterCode, parameterCode);
            parameter = this.getOne(queryWrapper);
        }
        return parameter;
    }
}
