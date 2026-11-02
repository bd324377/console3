package com.console.api.service;

import com.console.api.dto.res.LoadLayoutRes;

import java.util.Map;

public interface LoadAPIService {
    Map<String,Object> getProductInfo();
    LoadLayoutRes getLayoutInfo();
}
