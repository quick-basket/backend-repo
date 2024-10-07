package com.grocery.quickbasket.payment.controller;

import com.grocery.quickbasket.payment.dto.PaymentDto;
import com.grocery.quickbasket.payment.dto.PaymentListResponseDto;
import com.grocery.quickbasket.payment.dto.PaymentRequestDto;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.service.PaymentService;
import com.grocery.quickbasket.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{transactionId}/proof")
    public ResponseEntity<?> uploadPaymentProof(@PathVariable("transactionId") String transactionId, @RequestParam("file") MultipartFile file) {
        Payment payment = paymentService.uploadPaymentProof(transactionId, file);
        return Response.successResponse("Upload success", PaymentDto.fromEntity(payment));
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
}
