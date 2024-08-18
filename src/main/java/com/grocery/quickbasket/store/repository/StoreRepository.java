package com.grocery.quickbasket.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.store.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Long>{

}
