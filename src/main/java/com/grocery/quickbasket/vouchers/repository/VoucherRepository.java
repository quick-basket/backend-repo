package com.grocery.quickbasket.vouchers.repository;

import java.util.Optional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.vouchers.entity.Voucher;

public interface VoucherRepository extends JpaRepository<Voucher, Long>{
    List<Voucher> findAllByDeletedAtIsNull();
    Optional<Voucher> findByIdAndDeletedAtIsNull(Long id);
    Optional<Voucher> findByCode(String code);
    List<Voucher> findByMinPurchaseLessThanEqualAndEndDateAfterAndDeletedAtIsNull(BigDecimal amount, Instant now);


}
