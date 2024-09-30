package com.grocery.quickbasket.store.repository;

import com.grocery.quickbasket.store.entity.StoreAdmin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreAdminRepository extends JpaRepository<StoreAdmin, Long> {
    boolean existsByUserId(Long userId);
    Optional<StoreAdmin> findByUserId(Long userId);
    Optional<StoreAdmin> findByStoreId(Long storeId);
    Optional<StoreAdmin> findByIdAndDeletedAtIsNull(Long storeAdminId);
    List<StoreAdmin> findByDeletedAtIsNull();

}
