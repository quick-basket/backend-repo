package com.grocery.quickbasket.inventoryJournals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.inventoryJournals.entity.InventoryJournal;

public interface InventoryJournalRepository extends JpaRepository<InventoryJournal, Long> {

}
