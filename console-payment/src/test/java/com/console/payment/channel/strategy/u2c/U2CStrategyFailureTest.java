package com.console.payment.channel.strategy.u2c;

import com.console.payment.channel.model.ChannelResult;
import com.console.payment.channel.strategy.u2c.dto.req.QueryOrderReq;
import com.console.payment.channel.strategy.u2c.dto.res.PayRes;
import com.console.payment.channel.strategy.u2c.dto.res.U2CResponse;
import com.console.payment.entity.PaymentChannel;
import com.console.payment.entity.PaymentOrder;
import com.console.payment.utils.PaymentUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Request;
import org.junit.jupiter.api.Test;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class U2CStrategyFailureTest {
    @Test
    void rejectsPaymentAndCashOutCallbacksBeforeParsingWhenSignatureIsInvalid() {
        U2CStrategy strategy = new U2CStrategy(mock(U2CPaymentApi.class), new ObjectMapper());
        Map<String, Object> payload = callbackPayload();
        payload.put("sign", "invalid");

        assertThrows(IllegalArgumentException.class,
                () -> strategy.parsePaymentCallback(payload, paymentChannel()));
        assertThrows(IllegalArgumentException.class,
                () -> strategy.parseCashOutCallback(payload, paymentChannel()));
    }

    @Test
    void parsesPaymentAndCashOutCallbacksAfterSignatureVerification() {
        U2CStrategy strategy = new U2CStrategy(mock(U2CPaymentApi.class), new ObjectMapper());
        PaymentChannel channel = paymentChannel();
        Map<String, Object> payload = callbackPayload();
        payload.put("sign", PaymentUtils.sign(payload, channel.getApiSecret()));

        assertTrue(strategy.parsePaymentCallback(payload, channel).isSuccess());
        assertTrue(strategy.parseCashOutCallback(payload, channel).isSuccess());
    }

    @Test
    void convertsNetworkFailureToRetryableChannelResult() throws IOException {
        U2CPaymentApi api = mock(U2CPaymentApi.class);
        @SuppressWarnings("unchecked")
        Call<U2CResponse<PayRes>> call = mock(Call.class);
        when(api.queryPayment(anyString(), any(QueryOrderReq.class))).thenReturn(call);
        when(call.request()).thenReturn(new Request.Builder().url("https://u2c.example/query").build());
        when(call.execute()).thenThrow(new IOException("timeout"));

        ChannelResult result = new U2CStrategy(api, new ObjectMapper())
                .queryPayment(paymentOrder(), paymentChannel());

        assertFalse(result.isSuccess());
        assertTrue(result.isRetryable());
        assertEquals("NETWORK_ERROR", result.getErrorCode());
    }

    @Test
    void preservesChannelBusinessErrorWithoutThrowing() throws IOException {
        U2CPaymentApi api = mock(U2CPaymentApi.class);
        @SuppressWarnings("unchecked")
        Call<U2CResponse<PayRes>> call = mock(Call.class);
        U2CResponse<PayRes> channelResponse = new U2CResponse<>();
        channelResponse.setSuccess(false);
        channelResponse.setErrorCode("ORDER_NOT_FOUND");
        channelResponse.setMessage("order does not exist");
        when(api.queryPayment(anyString(), any(QueryOrderReq.class))).thenReturn(call);
        when(call.request()).thenReturn(new Request.Builder().url("https://u2c.example/query").build());
        when(call.execute()).thenReturn(Response.success(channelResponse));

        ChannelResult result = new U2CStrategy(api, new ObjectMapper())
                .queryPayment(paymentOrder(), paymentChannel());

        assertFalse(result.isSuccess());
        assertFalse(result.isRetryable());
        assertEquals("ORDER_NOT_FOUND", result.getErrorCode());
    }

    private PaymentOrder paymentOrder() {
        PaymentOrder order = new PaymentOrder();
        order.setMerchantOrderNo("PAY_1_1");
        return order;
    }

    private PaymentChannel paymentChannel() {
        PaymentChannel channel = new PaymentChannel();
        channel.setAppKey("merchant");
        channel.setApiSecret("secret");
        channel.setPaySearchUrl("https://u2c.example/query");
        return channel;
    }

    private Map<String, Object> callbackPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("merchantId", "merchant");
        payload.put("merchantOrderNo", "PAY_1_1");
        payload.put("orderNo", "U2C-1001");
        payload.put("amount", 1000);
        payload.put("status", "PAID");
        payload.put("currency", "BRL");
        return payload;
    }
}
