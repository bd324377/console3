package com.console.activity.service.impl;

import com.console.activity.entity.ActivityProc;
import com.console.activity.mapper.ActivityProcMapper;
import com.console.activity.service.ActivityProcService;
import com.console.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ActivityProcServiceImpl extends BaseServiceImpl<ActivityProcMapper,ActivityProc> implements ActivityProcService {
}
