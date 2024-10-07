package com.grocery.quickbasket.order.mapper;

import com.grocery.quickbasket.order.dto.OrderListDetailDto;
import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderItem;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    private final PaymentService paymentService;

    public OrderMapper(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public OrderListDetailDto mapToOrderListResponseDto(Page<Order> orderPage) {
        List<OrderListDetailDto.OrderDetailDto> orderDtos = orderPage.getContent().stream()
                .map(this::mapToOrderDetailDto)
                .collect(Collectors.toList());

        return OrderListDetailDto.builder()
                .orders(orderDtos)
                .pagination(createPaginationDto(orderPage))
                .build();
    }

    private OrderListDetailDto.OrderDetailDto mapToOrderDetailDto(Order order) {
        Payment payment = paymentService.getPayment(order.getOrderCode());
        return OrderListDetailDto.OrderDetailDto.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .storeName(order.getStore().getName())
                .totalAmount(order.getTotalAmount())
                .orderStatus(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .shippingMethod(order.getShippingMethod())
                .payment(mapToPaymentDto(payment))
                .items(mapToOrderItemDtos(order.getItems()))
                .build();
    }

    private OrderListDetailDto.PaymentDto mapToPaymentDto(Payment payment) {
        return OrderListDetailDto.PaymentDto.builder()
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getPaymentStatus().name())
                .amount(payment.getAmount())
                .build();
    }

    private List<OrderListDetailDto.OrderItemDto> mapToOrderItemDtos(List<OrderItem> items) {
        return items.stream()
                .map(this::mapToOrderItemDto)
                .collect(Collectors.toList());
    }

    private OrderListDetailDto.OrderItemDto mapToOrderItemDto(OrderItem item) {
        return OrderListDetailDto.OrderItemDto.builder()
                .productName(item.getProduct().getName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }

    private OrderListDetailDto.Pagination createPaginationDto(Page<?> page) {
        return OrderListDetailDto.Pagination.builder()
                .currentPage(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalItems(page.getTotalElements())
                .itemsPerPage(page.getSize())
                .build();
    }
}
