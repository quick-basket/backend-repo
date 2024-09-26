package com.grocery.quickbasket.user.repository;

import com.grocery.quickbasket.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByReferralCode(String referralCode);
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT sa.user.id FROM StoreAdmin sa)")
    List<User> findAllUsersNotInStoreAdmins();
}
