package com.grocery.quickbasket.store.repository;

import com.grocery.quickbasket.store.entity.StoreAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreAdminRepository extends JpaRepository<StoreAdmin, Long> {
    boolean existsByUserId(Long userId);
}
