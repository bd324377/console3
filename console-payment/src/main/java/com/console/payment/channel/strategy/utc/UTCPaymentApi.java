package com.console.payment.channel.strategy.utc;

import com.console.payment.channel.strategy.utc.model.PayResult;
import com.console.payment.channel.strategy.utc.model.UTCResponse;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

@RetrofitClient(connectTimeoutMs = 5000, readTimeoutMs = 10000)
public interface UTCPaymentApi {
    @POST
    @FormUrlEncoded
    Call<UTCResponse<PayResult>> createPayOrder(@Url String fullUrl);
}
