package com.grocery.quickbasket.inventoryJournal.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.inventoryJournal.dto.InventoryJournalDto;
import com.grocery.quickbasket.inventoryJournal.service.InventoryJournalService;
import com.grocery.quickbasket.response.Response;

@RestController
@RequestMapping("/api/v1/inventory-journals")
public class InventoryJournalController {

    private final InventoryJournalService inventoryJournalService;

    public InventoryJournalController(InventoryJournalService inventoryJournalService) {
        this.inventoryJournalService = inventoryJournalService;
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getAllByStoreId(@PathVariable Long storeId) {
        List<InventoryJournalDto> journalDtos = inventoryJournalService.getAllByStoreId(storeId);
        return Response.successResponse("fetch all journals", journalDtos);
    }

    @GetMapping("/{inventoryId}/in")
    public ResponseEntity<?> getTotalIn(@PathVariable Long inventoryId) {
        int totalIn = inventoryJournalService.getTotalIn(inventoryId);
        return Response.successResponse("get all total in", totalIn);
    }

    @GetMapping("/{inventoryId}/out")
    public ResponseEntity<?> getTotalOut(@PathVariable Long inventoryId) {
        int totalOut = inventoryJournalService.getTotalOut(inventoryId);
        return Response.successResponse("get all total out", totalOut);
    }
}
