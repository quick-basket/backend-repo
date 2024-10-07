package com.grocery.quickbasket.inventoryJournal.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.grocery.quickbasket.inventoryJournal.entity.InventoryJournal;

public interface InventoryJournalRepository extends JpaRepository<InventoryJournal, Long> {
    @Query("SELECT ij FROM InventoryJournal ij JOIN ij.inventory i WHERE i.store.id = :storeId")
    List<InventoryJournal> findAllByStoreId(@Param("storeId") Long storeId);

    @Query("SELECT COALESCE(SUM(ij.quantityChange), 0) FROM InventoryJournal ij WHERE ij.inventory.id = :inventoryId AND ij.quantityChange > 0")
    int getTotalPositiveQuantityChange(@Param("inventoryId") Long inventoryId);

    @Query("SELECT COALESCE(SUM(ij.quantityChange), 0) FROM InventoryJournal ij WHERE ij.inventory.id = :inventoryId AND ij.quantityChange < 0")
    int getTotalNegativeQuantityChange(@Param("inventoryId") Long inventoryId);
}
