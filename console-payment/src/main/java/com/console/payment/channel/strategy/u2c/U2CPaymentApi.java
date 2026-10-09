package com.console.payment.channel.strategy.u2c;

import com.console.payment.channel.strategy.u2c.req.CashOutOrderReq;
import com.console.payment.channel.strategy.u2c.req.PayOrderReq;
import com.console.payment.channel.strategy.u2c.req.QueryBalanceReq;
import com.console.payment.channel.strategy.u2c.req.QueryOrderReq;
import com.console.payment.channel.strategy.u2c.res.BalanceRes;
import com.console.payment.channel.strategy.u2c.res.CashOutRes;
import com.console.payment.channel.strategy.u2c.res.PayRes;
import com.console.payment.channel.strategy.u2c.res.U2CResponse;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import com.github.lianjiatech.retrofit.spring.boot.retry.Retry;
import com.github.lianjiatech.retrofit.spring.boot.log.Logging;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Url;

import java.util.List;

/**
 * U2C 渠道 Retrofit 接口定义。
 *
 * <p>渠道地址保存在 {@code Channel} 中，因此使用 {@link Url} 传入完整地址。
 * 所有请求均为 JSON，不在 Retrofit 层配置自动重试，避免创建订单被重复提交。</p>
 */
@Retry(enable = false)
@Logging(enable = false)
@RetrofitClient(baseUrl = "http://placeholder",connectTimeoutMs = 5_000, readTimeoutMs = 10_000)
public interface U2CPaymentApi {
    /** 创建充值订单。 */
    @POST
    Call<U2CResponse<PayRes>> createPayment(@Url String url, @Body PayOrderReq request);

    /** 查询充值订单。 */
    @POST
    Call<U2CResponse<PayRes>> queryPayment(@Url String url, @Body QueryOrderReq request);

    /** 创建提现订单。 */
    @POST
    Call<U2CResponse<CashOutRes>> createCashOut(@Url String url, @Body CashOutOrderReq request);

    /** 查询提现订单。 */
    @POST
    Call<U2CResponse<CashOutRes>> queryCashOut(@Url String url, @Body QueryOrderReq request);

    /** 查询商户各币种账户余额。 */
    @POST
    Call<U2CResponse<List<BalanceRes>>> queryBalance(@Url String url, @Body QueryBalanceReq request);
}
