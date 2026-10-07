package com.console.payment.channel.strategy.u2c.dto;

import com.console.payment.channel.strategy.u2c.dto.req.CashOutOrderReq;
import com.console.payment.entity.BankCard;
import com.console.payment.entity.CashOutOrder;
import com.console.payment.entity.PaymentChannel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CashOutOrderReqTest {
    @ParameterizedTest
    @ValueSource(strings = {"PIX_PHONE", "PIX_EMAIL", "PIX_CPF", "PIX_CNPJ", "PIX_RANDOM", "PIX_EVP", "PIX_BANK"})
    void supportsBrazilPixTypes(String type) {
        CashOutOrder order = new CashOutOrder();
        order.setMerchantOrderNo("CASH_1_1");
        order.setOrderAmt(new BigDecimal("10.00"));
        PaymentChannel channel = new PaymentChannel();
        channel.setAppKey("merchant");
        channel.setApiSecret("secret");
        channel.setCurType("BRL");
        channel.setCashOutCallBackUrl("https://merchant.example/callback");
        BankCard card = new BankCard();
        card.setAccountType(type);
        card.setAccountName("Receiver");
        card.setAccountNo(accountNo(type));
        card.setBankCode("12345678");
        card.setBranchBankNo("0001");

        CashOutOrderReq dto = assertDoesNotThrow(() -> new CashOutOrderReq(order, channel, card, "CASH"));
        assertEquals("PIX_RANDOM".equals(type) ? "PIX_EVP" : type, dto.getAccountType());
        assertEquals(1000, dto.getAmount());
    }

    private String accountNo(String type) {
        return switch (type) {
            case "PIX_PHONE" -> "+5511999999999";
            case "PIX_EMAIL" -> "receiver@example.com";
            case "PIX_CPF" -> "12345678901";
            case "PIX_CNPJ" -> "12345678901234";
            default -> "random-pix-key";
        };
    }
}
