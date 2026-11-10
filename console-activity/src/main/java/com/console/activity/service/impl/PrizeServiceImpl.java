package com.console.activity.service.impl;

import com.console.activity.entity.Prize;
import com.console.activity.mapper.PrizeMapper;
import com.console.activity.service.PrizeService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PrizeServiceImpl extends BaseServiceImpl<PrizeMapper, Prize> implements PrizeService {
}
