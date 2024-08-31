package com.grocery.quickbasket.inventoryJournal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.inventoryJournal.dto.InventoryJournalDto;
import com.grocery.quickbasket.inventoryJournal.service.InventoryJournalService;

@RestController
@RequestMapping("/api/v1/inventory-journals")
public class InventoryJournalController {

    private final InventoryJournalService inventoryJournalService;

    public InventoryJournalController(InventoryJournalService inventoryJournalService) {
        this.inventoryJournalService = inventoryJournalService;
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<List<?>> getAllByStoreId(@PathVariable Long storeId) {
        List<InventoryJournalDto> journalDtos = inventoryJournalService.getAllByStoreId(storeId);
        return ResponseEntity.ok(journalDtos);
    }
}
