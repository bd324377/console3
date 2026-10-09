package com.console.payment.channel.strategy.fourz;

import com.console.payment.channel.strategy.fourz.req.*;
import com.console.payment.channel.strategy.fourz.res.*;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.log.Logging;
import com.github.lianjiatech.retrofit.spring.boot.retry.Retry;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;
import java.util.List;

@Retry(enable = false)
@Logging(enable = false)
@RetrofitClient(baseUrl = "http://placeholder", connectTimeoutMs = 5_000, readTimeoutMs = 10_000)
public interface FourZPaymentApi {
    @POST
    Call<FourZResponse<PayRes>> createPayment(@Url String url, @Body PayOrderReq request);

    @POST
    Call<FourZResponse<PayRes>> queryPayment(@Url String url, @Body QueryOrderReq request);

    @POST
    Call<FourZResponse<CashOutRes>> createCashOut(@Url String url, @Body CashOutOrderReq request);

    @POST
    Call<FourZResponse<CashOutRes>> queryCashOut(@Url String url, @Body QueryOrderReq request);

    @POST
    Call<FourZResponse<List<BalanceRes>>> queryBalance(@Url String url, @Body QueryBalanceReq request);
}
