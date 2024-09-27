package com.grocery.quickbasket.order.repository;

import com.grocery.quickbasket.order.entity.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COALESCE(SUM(DISTINCT o.totalAmount), 0) FROM OrderItem oi JOIN oi.order o")
    BigDecimal sumTotalAmountFromAllOrders();

    @Query("SELECT COALESCE(SUM(DISTINCT o.totalAmount), 0) FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.createdAt >= :oneWeekAgo")
    BigDecimal sumTotalAmountFromOrdersLastWeek(@Param("oneWeekAgo") Instant oneWeekAgo);
    
    @Query("SELECT COALESCE(SUM(DISTINCT o.totalAmount), 0) FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.createdAt >= :oneMonthAgo")
    BigDecimal sumTotalAmountFromOrdersLastMonth(@Param("oneMonthAgo") Instant oneMonthAgo);

    @Query("SELECT COALESCE(SUM(oi.quantity * oi.price), 0) FROM OrderItem oi " +
       "JOIN oi.order o " +
       "JOIN oi.product p " +
       "JOIN p.category pc " +
       "WHERE o.store.id = :storeId " +
       "AND pc.id = :categoryId")
BigDecimal getTotalAmountByStoreAndCategory(
    @Param("storeId") Long storeId,
    @Param("categoryId") Long categoryId
);

    @Query("SELECT COALESCE(SUM(oi.quantity * oi.price), 0) FROM OrderItem oi " +
           "JOIN oi.order o " +
           "JOIN oi.product p " +
           "WHERE o.store.id = :storeId " +
           "AND p.id = :productId")
    BigDecimal getTotalAmountByStoreAndProduct(
        @Param("storeId") Long storeId,
        @Param("productId") Long productId
    );

    @Query("SELECT COALESCE(SUM(DISTINCT o.totalAmount), 0) FROM OrderItem oi " +
        "JOIN oi.order o " +
        "WHERE o.store.id = :storeId")
    BigDecimal getTotalAmountByStore(
        @Param("storeId") Long storeId
    );

}
