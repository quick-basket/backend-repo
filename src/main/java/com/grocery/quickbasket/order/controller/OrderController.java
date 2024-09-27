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

import java.math.BigDecimal;
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

    @GetMapping("/store/{storeId}")
    public ResponseEntity<?> getAllOrderByStoreAndUserId(@PathVariable Long storeId) {
        List<OrderListResponseDto> orders = orderService.getAllOrderByStoreIdAndUserId(storeId);
        return Response.successResponse("success fetch all order", orders);
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<?> updateOrderStatus (@PathVariable Long orderId, @RequestBody OrderStatusUpdateRequest request) {
        OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, request.getNewStatus());
        return Response.successResponse("order status updated", updatedOrder);
    }

    @GetMapping("/total-amounts-all-store")
    public ResponseEntity<?> getTotalAmountAllStore() {
        BigDecimal totalAmount = orderService.getTotalAmountAllStore();
        return Response.successResponse("get total amount", totalAmount);
    }

    @GetMapping("/total-amount-last-week")
    public ResponseEntity<?> getTotalAmountFromOrdersLastWeek() {
        BigDecimal totalAmount = orderService.getTotalAmountFromOrdersLastWeek();
        return Response.successResponse("get total amount", totalAmount);
    }
    @GetMapping("/total-amount-last-month")
    public ResponseEntity<?> getTotalAmountFromOrdersLastMonth() {
        BigDecimal totalAmount = orderService.getTotalAmountFromOrdersLastMonth();
        return Response.successResponse("get total amount", totalAmount);
    }

    @GetMapping("/total-amount-storeid-categoryid")
    public ResponseEntity<?> getTotalAmountByStoreAndCategory(
            @RequestParam Long storeId,
            @RequestParam Long categoryId) {
        BigDecimal totalAmount = orderService.getTotalAmountByStoreAndCategory(storeId, categoryId);
        return Response.successResponse("get total amount", totalAmount);
    }

    @GetMapping("/total-amount-storeid-productId")
    public ResponseEntity<?> getTotalAmountByStoreAndProduct(
            @RequestParam Long storeId,
            @RequestParam Long productId) {
        BigDecimal totalAmount = orderService.getTotalAmountByStoreAndProduct(storeId, productId);
        return Response.successResponse("get total amount", totalAmount);
    }
    @GetMapping("/total-amounts-storeid")
    public ResponseEntity<?> getTotalAmountByStoreId(
            @RequestParam Long storeId) {
        BigDecimal totalAmount = orderService.getTotalAmountByStoreId(storeId);
        return Response.successResponse("get total amount", totalAmount);
    }
}
