package com.grocery.quickbasket.midtrans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MidtransResponse {
    private String statusMessage;
    private String transactionId;
    private String fraudStatus;
    private String transactionStatus;
    private String statusCode;
    private String merchantId;
    private String grossAmount;
    private String paymentType;
    private String transactionTime;
    private String currency;
    private String expiryTime;
    private String orderId;
    private List<Action> actions;
    private List<VaNumber> vaNumbers;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Action {
        private String method;
        private String name;
        private String url;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VaNumber {
        private String bank;
        private String vaNumber;
    }
}