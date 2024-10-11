package com.grocery.quickbasket.config;

import com.grocery.quickbasket.order.entity.Order;
import com.grocery.quickbasket.order.entity.OrderStatus;
import com.grocery.quickbasket.order.repository.OrderRepository;
import com.grocery.quickbasket.order.service.OrderService;
import com.grocery.quickbasket.payment.entity.Payment;
import com.grocery.quickbasket.payment.entity.PaymentStatus;
import com.grocery.quickbasket.payment.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final OrderService orderService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public SchedulerConfig(OrderService orderService, PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedRate = 300000) // Run Every 5 minutes
    public void scheduleProcessingToDeliveredUpdate() {
        log.info("Scheduled to delivered update");
        orderService.updateProcessingOrdersToDelivered();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleDeliveredToCompletedUpdate() {
        orderService.updateDeliveredOrdersToCompleted();
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkAndUpdateExpiredPayments() {
        log.info("Scheduled to check expired payments");
        Instant thirtyMinutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES);
        List<Payment> expiredPayments = paymentRepository.findExpiredManualPayments(
                PaymentStatus.PAYMENT_CONFIRMATION,
                "manual",
                thirtyMinutesAgo
        );

        for (Payment payment : expiredPayments) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            Order order = payment.getOrder();
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
        }

        paymentRepository.saveAll(expiredPayments);
    }
}
