package com.grocery.quickbasket.vouchers.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.vouchers.entity.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long>{
    List<Voucher> findAllByDeletedAtIsNull();
    Optional<Voucher> findByIdAndDeletedAtIsNull(Long id);
}
