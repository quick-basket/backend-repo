package com.grocery.quickbasket.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.inventory.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{

}
