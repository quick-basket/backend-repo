package com.grocery.quickbasket.midtrans.service;

import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.entity.Order;
import com.midtrans.httpclient.error.MidtransError;
import org.json.JSONObject;

import java.util.Map;

public interface MidtransService {
    JSONObject createTransaction(Map<String, Object> transactionDetails) throws MidtransError;
    JSONObject getTransactionStatus(String orderId) throws MidtransError;
    Map<String, Object> buildMidtransRequest(Order order, CheckoutDto checkoutData, String paymentType);
    Map<String, Object> createOrRetrieveMidtransTransaction(Order order, CheckoutDto checkoutData, String paymentType) throws MidtransError;
    void handleNotification(Map<String, Object> notificationPayload) throws MidtransError;
}
