package com.console.payment.channel.strategy.u2c;

import com.console.payment.channel.strategy.u2c.model.PayResult;
import com.console.payment.channel.strategy.u2c.model.U2CResponse;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;

@RetrofitClient(connectTimeoutMs = 5000, readTimeoutMs = 10000)
public interface U2CPaymentApi {
    @POST
    @FormUrlEncoded
    Call<U2CResponse<PayResult>> createPayOrder(@Url String fullUrl);
}
