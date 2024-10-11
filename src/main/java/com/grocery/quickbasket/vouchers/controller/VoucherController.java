package com.grocery.quickbasket.vouchers.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grocery.quickbasket.response.Response;
import com.grocery.quickbasket.vouchers.dto.VoucherRequestDto;
import com.grocery.quickbasket.vouchers.dto.VoucherResponseDto;
import com.grocery.quickbasket.vouchers.service.VoucherService;

@RestController
@RequestMapping("/api/v1/vouchers")
public class VoucherController {

    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createVoucher (@RequestBody VoucherRequestDto requestDto) {
        VoucherResponseDto createVoucher = voucherService.createVoucher(requestDto);
        return Response.successResponse("voucher created", createVoucher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editVoucher (@PathVariable Long id, @RequestBody VoucherRequestDto requestDto) {
        VoucherResponseDto updateVoucher = voucherService.updateVoucher(id, requestDto);
        return Response.successResponse("voucher udpated", updateVoucher);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById (@PathVariable Long id) {
        return Response.successResponse("get voucher", voucherService.getVoucherById(id));
    }

    @GetMapping("/userId")
    public ResponseEntity<?>getAllVouchersByUserId() {
        return Response.successResponse("get all vouchers", voucherService.getAllVouchersByUserId());
    }
    @GetMapping
    public ResponseEntity<?>getAllVouchers() {
        return Response.successResponse("get all vouchers", voucherService.getAllVouchers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVoucher(@PathVariable Long id) {
        voucherService.deleteVoucher(id);
        return ResponseEntity.noContent().build();    
    }
}
