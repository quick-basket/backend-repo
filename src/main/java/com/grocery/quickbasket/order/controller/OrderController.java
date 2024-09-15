package com.grocery.quickbasket.order.controller;

import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.dto.SnapTokenResponse;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.response.Response;
import com.midtrans.httpclient.error.MidtransError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/checkout")
    public ResponseEntity<?> getCheckoutOrderSummary () {
        return Response.successResponse("Summary fetched", orderService.createCheckoutSummaryFromCart());
    }

    @PostMapping("/initiate")
    public ResponseEntity<?> initiateOrder (@RequestBody CheckoutDto checkoutDto) {
        try {
            SnapTokenResponse response = orderService.initiateSnapTransaction(checkoutDto);
            return Response.successResponse("Transaction successfully initiated", response);
        } catch (MidtransError e) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "MIDTRANS ERROR", e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder (@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        if (order == null) {
            return Response.failedResponse("Order not found", HttpStatus.NOT_FOUND);
        } else {
            return Response.successResponse("Order found", order);
        }
    }
}
