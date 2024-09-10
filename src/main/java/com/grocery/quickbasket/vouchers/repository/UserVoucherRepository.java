package com.grocery.quickbasket.vouchers.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.vouchers.entity.UserVoucher;

public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

}
