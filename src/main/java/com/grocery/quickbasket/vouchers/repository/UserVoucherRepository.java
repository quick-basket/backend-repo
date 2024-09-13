package com.grocery.quickbasket.vouchers.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.vouchers.entity.UserVoucher;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    List<UserVoucher> findByUserIdAndIsUsedFalse (Long userId);
    Optional<UserVoucher> findByIdAndUserIdAndIsUsedFalse(Long userVoucherId, Long userId);
}
