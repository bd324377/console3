package com.console.user.controller;

import com.console.user.service.AvatarService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    @Resource
    private AvatarService avatarService;
}
