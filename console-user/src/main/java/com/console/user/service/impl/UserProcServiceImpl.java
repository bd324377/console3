package com.console.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.console.core.entity.UserProc;
import com.console.core.mapper.UserProcMapper;
import com.console.user.service.UserProcService;
import org.springframework.stereotype.Service;

@Service
public class UserProcServiceImpl extends ServiceImpl<UserProcMapper, UserProc> implements UserProcService {
}
