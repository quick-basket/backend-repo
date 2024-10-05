package com.grocery.quickbasket.payments.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.payments.dto.PaymentListResponseDto;
import com.grocery.quickbasket.payments.dto.PaymentRequestDto;
import com.grocery.quickbasket.payments.entity.Payment;
import com.grocery.quickbasket.payments.service.PaymentService;
import com.grocery.quickbasket.response.Response;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController (PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatepayment (@PathVariable Long id, @RequestBody PaymentRequestDto requestDto) {
        PaymentListResponseDto updateDto = paymentService.updatePayment(id, requestDto);
        return Response.successResponse("payment updated", updateDto);
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getAllPaymentByStoreId(@PathVariable Long storeId){
        List<PaymentListResponseDto> paymentListDto = paymentService.getAllPaymentListByStoreId(storeId);
        return Response.successResponse("fetched all payment data", paymentListDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllPayments() {
        List<PaymentListResponseDto> payments = paymentService.getAllPayment();
        return Response.successResponse("get all payment", payments);
    }
}
