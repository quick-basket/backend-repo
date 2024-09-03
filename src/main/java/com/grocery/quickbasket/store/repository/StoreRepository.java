package com.grocery.quickbasket.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.store.entity.Store;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long>{
    boolean existsByName(String name);
    List<Store> getStoreByDeletedAtIsNull();
}
