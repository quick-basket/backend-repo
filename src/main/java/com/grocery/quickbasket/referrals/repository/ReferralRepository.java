package com.grocery.quickbasket.referrals.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grocery.quickbasket.referrals.entity.Referrals;

public interface ReferralRepository extends JpaRepository<Referrals, Long> {

}
