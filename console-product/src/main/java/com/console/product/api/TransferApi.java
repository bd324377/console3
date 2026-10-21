package com.console.product.api;

import com.alibaba.fastjson.JSONObject;
import com.console.framework.domain.Result;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.log.Logging;
import com.github.lianjiatech.retrofit.spring.boot.retry.Retry;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

@Retry(enable = false)
@Logging(enable = false)
@RetrofitClient(baseUrl = "http://127.0.0.1:8080",connectTimeoutMs = 5_000, readTimeoutMs = 10_000)
public interface TransferApi {
    @POST
    Call<Result> syncTransferData(//同步中转平台数据（产品信息，产品订单信息等）
            @Url String url,
            @Header("x-apiKey") String merchantCode,
            @Header("x-timestamp") String timestamp,
            @Header("x-sign") String random,
            @Body JSONObject param);
    @POST
    Call<Result> queryTransferRecord(//查询指定转账记录
            @Url String url,
            @Header("x-apiKey") String merchantCode,
            @Header("x-timestamp") String timestamp,
            @Header("x-sign") String random,
            @Body JSONObject param
    );
    @POST
    Call<Result> enterProduct(//进入中转平台产品
            @Url String url,
            @Header("x-apiKey") String merchantCode,
            @Header("x-timestamp") String timestamp,
            @Header("x-sign") String random,
            @Body JSONObject param
    );

    @POST
    Call<Result> logoutTransfer(//退出中转平台
           @Url String url,
           @Header("x-apiKey") String merchantCode,
           @Header("x-timestamp") String timestamp,
           @Header("x-sign") String random,
           @Body JSONObject param
    );

}
