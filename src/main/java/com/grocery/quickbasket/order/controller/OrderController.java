package com.grocery.quickbasket.order.controller;

import com.grocery.quickbasket.order.dto.CheckoutDto;
import com.grocery.quickbasket.order.dto.OrderListResponseDto;
import com.grocery.quickbasket.order.dto.OrderResponseDto;
import com.grocery.quickbasket.order.dto.OrderStatusUpdateRequest;
import com.grocery.quickbasket.order.dto.SnapTokenResponse;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.response.Response;
import com.midtrans.httpclient.error.MidtransError;

import java.util.List;

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

    @PostMapping("/initiate/{orderId}")
    public ResponseEntity<?> initiateOrder (@PathVariable Long orderId) {
        try {
            SnapTokenResponse response = orderService.initiateSnapTransaction(orderId);
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

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getAllOrderByStoreAndUserId(@PathVariable Long storeId) {
        List<OrderListResponseDto> orders = orderService.getAllOrderByStoreIdAndUserId(storeId);
        return Response.successResponse("success fetch all order", orders);
    }

    @PostMapping("/create-pending")
    public ResponseEntity<?> createPendingOrder(@RequestBody CheckoutDto checkoutData) {
        OrderResponseDto pendingOrder = orderService.createOrRetrievePendingOrder(checkoutData);
        return Response.successResponse("order created", pendingOrder);
    }

    @PutMapping("/status-payment/{orderId}")
    public ResponseEntity<?> updateOrderStatusAfterPayment(@PathVariable Long orderId, @RequestBody String paymentStatus) {
        return Response.successResponse("order updated", orderService.updateOrderStatusAfterPayment(orderId, paymentStatus));
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateRequest orderStatusUpdateRequest) {
        return Response.successResponse("order updated", orderService.updateOrderStatus(orderId, orderStatusUpdateRequest.getNewStatus()));
    }

//    @PostMapping("/generate-snap-token/{orderId}")
//    public ResponseEntity<?> generateSnapToken(@PathVariable Long orderId) throws MidtransError {
//        SnapTokenResponse response = orderService.generateSnapToken(orderId);
//        return Response.successResponse("Generate Snap", response);
//    }
}
