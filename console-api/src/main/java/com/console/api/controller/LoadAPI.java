package com.console.api.controller;

import com.console.api.service.LoadAPIService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoadAPI {
    @Resource
    private LoadAPIService loadAPIService;
}
