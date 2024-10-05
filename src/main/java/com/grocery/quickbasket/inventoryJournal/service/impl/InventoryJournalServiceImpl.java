package com.grocery.quickbasket.inventoryJournal.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.grocery.quickbasket.inventoryJournal.dto.InventoryJournalDto;
import com.grocery.quickbasket.inventoryJournal.entity.InventoryJournal;
import com.grocery.quickbasket.inventoryJournal.repository.InventoryJournalRepository;
import com.grocery.quickbasket.inventoryJournal.service.InventoryJournalService;

@Service
public class InventoryJournalServiceImpl implements InventoryJournalService{

    private final InventoryJournalRepository inventoryJournalRepository;

    public InventoryJournalServiceImpl(InventoryJournalRepository inventoryJournalRepository) {
        this.inventoryJournalRepository = inventoryJournalRepository;
    }

    @Override
    public List<InventoryJournalDto> getAllByStoreId(Long storeId) {
        List<InventoryJournal> journals = inventoryJournalRepository.findAllByStoreId(storeId);
        return journals.stream()
            .map(InventoryJournalDto::mapToDto)
            .collect(Collectors.toList());
    }

    @Override
    public int getTotalIn(Long inventoryId) {
        return inventoryJournalRepository.getTotalPositiveQuantityChange(inventoryId);
    }

    @Override
    public int getTotalOut(Long inventoryId) {
        return inventoryJournalRepository.getTotalNegativeQuantityChange(inventoryId);
    }

}
