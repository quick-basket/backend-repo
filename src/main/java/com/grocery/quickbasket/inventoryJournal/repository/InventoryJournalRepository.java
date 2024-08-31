package com.grocery.quickbasket.inventoryJournal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grocery.quickbasket.inventoryJournal.entity.InventoryJournal;

public interface InventoryJournalRepository extends JpaRepository<InventoryJournal, Long> {
    @Query("SELECT ij FROM InventoryJournal ij JOIN ij.inventory i WHERE i.store.id = :storeId")
    List<InventoryJournal> findAllByStoreId(@Param("storeId") Long storeId);
}
