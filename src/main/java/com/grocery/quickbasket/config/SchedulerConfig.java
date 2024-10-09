package com.grocery.quickbasket.config;

import com.grocery.quickbasket.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class SchedulerConfig {
    private final OrderService orderService;

    public SchedulerConfig(OrderService orderService) {
        this.orderService = orderService;
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
}
