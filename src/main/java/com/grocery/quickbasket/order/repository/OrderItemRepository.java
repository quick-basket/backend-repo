package com.grocery.quickbasket.order.repository;

import com.grocery.quickbasket.order.entity.OrderItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
       "FROM Order o " +
       "WHERE o.id IN (SELECT DISTINCT oi.order.id FROM OrderItem oi) " +
       "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal sumTotalAmountFromAllOrders();

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
       "FROM Order o " +
       "WHERE o.id IN (SELECT DISTINCT oi.order.id FROM OrderItem oi) " +
       "AND o.createdAt >= :oneWeekAgo " +
       "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal sumTotalAmountFromOrdersLastWeek(@Param("oneWeekAgo") Instant oneWeekAgo);
    
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
       "FROM Order o " +
       "WHERE o.id IN (SELECT DISTINCT oi.order.id FROM OrderItem oi) " +
       "AND o.createdAt >= :oneMonthAgo " +
       "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal sumTotalAmountFromOrdersLastMonth(@Param("oneMonthAgo") Instant oneMonthAgo);

    @Query("SELECT COALESCE(SUM(oi.quantity * oi.price), 0) " +
       "FROM OrderItem oi " +
       "JOIN oi.order o " +
       "JOIN oi.product p " +
       "JOIN p.category pc " +
       "WHERE o.store.id = :storeId " +
       "AND pc.id = :categoryId " +
       "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalAmountByStoreAndCategory(
        @Param("storeId") Long storeId,
        @Param("categoryId") Long categoryId
    );

    @Query("SELECT COALESCE(SUM(oi.quantity * oi.price), 0) " +
       "FROM OrderItem oi " +
       "JOIN oi.order o " +
       "JOIN oi.product p " +
       "WHERE o.store.id = :storeId " +
       "AND p.id = :productId " +  
       "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalAmountByStoreAndProduct(
        @Param("storeId") Long storeId,
        @Param("productId") Long productId
    );

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
       "FROM Order o " +
       "WHERE o.id IN (SELECT DISTINCT oi.order.id FROM OrderItem oi WHERE oi.order.store.id = :storeId)" + 
       "AND o.status IN ('PROCESSING', 'SHIPPED', 'DELIVERED')")
    BigDecimal getTotalAmountByStore(
        @Param("storeId") Long storeId);

    List<OrderItem> findByOrderId(Long orderId);
}
