package com.grocery.quickbasket.inventoryJournal.service;

import java.util.List;

import com.grocery.quickbasket.inventoryJournal.dto.InventoryJournalDto;

public interface InventoryJournalService {

    List<InventoryJournalDto> getAllByStoreId(Long storeId);
    int getTotalIn(Long inventoryId);
    int getTotalOut(Long inventoryId);
}
