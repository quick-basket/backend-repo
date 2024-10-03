package com.grocery.quickbasket.order.controller;

import com.grocery.quickbasket.auth.helper.Claims;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.order.dto.*;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.response.Response;
import com.midtrans.httpclient.error.MidtransError;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @PostMapping()
    public ResponseEntity<?> createOrder(@RequestBody CheckoutDto checkoutData, @RequestParam String paymentType) {
        try {
            OrderWithMidtransResponseDto pendingOrder = orderService.createOrder(checkoutData, paymentType);
            return Response.successResponse("Order created or retrieved", pendingOrder);
        } catch (MidtransError e) {
            return Response.failedResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "MIDTRANS ERROR", e.getMessage());
        }
    }

    @PutMapping("/status-payment/{orderId}")
    public ResponseEntity<?> updateOrderStatusAfterPayment(@PathVariable String orderId, @RequestBody Map<String, String> payload) {
        String paymentStatus = payload.get("paymentStatus");
        try {
            OrderResponseDto updatedOrder = orderService.updateOrderStatusAfterPayment(orderId, paymentStatus);
            return Response.successResponse("Order updated", updatedOrder);
        } catch (DataNotFoundException | MidtransError e) {
            return Response.failedResponse("Order not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingOrders() {
        var claims = Claims.getClaimsFromJwt();
        Long userId = (Long) claims.get("userId");
        return Response.successResponse("Get pending order", orderService.getPendingOrder(userId));
    }

    @PutMapping("/status/{orderId}")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateRequest orderStatusUpdateRequest) {
        try {
            OrderResponseDto updatedOrder = orderService.updateOrderStatus(orderId, orderStatusUpdateRequest.getNewStatus());
            return Response.successResponse("Order updated", updatedOrder);
        } catch (DataNotFoundException e) {
            return Response.failedResponse("Order not found", HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status/{orderCode}")
    public ResponseEntity<?> getOrderStatus(@PathVariable String orderCode) throws MidtransError {
        return Response.successResponse("Got order status", orderService.getOrderStatus(orderCode));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(@PathVariable Long orderId) {
        try {
            Order cancelledOrder = orderService.cancelOrder(orderId);
            return Response.successResponse("Order cancelled", cancelledOrder);
        } catch (DataNotFoundException e) {
            return Response.failedResponse("Order not found", HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return Response.failedResponse("Cannot cancel order", HttpStatus.BAD_REQUEST);
        }
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
