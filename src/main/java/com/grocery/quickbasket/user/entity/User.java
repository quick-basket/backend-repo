package com.grocery.quickbasket.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.security.SecureRandom;
import java.time.Instant;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "img_profile")
    private String imgProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.user;

    @ColumnDefault("false")
    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Size(max = 10)
    @Column(name = "referral_code", unique = true)
    private String referralCode;

    @Column(name = "point_balance", columnDefinition = "DOUBLE DEFAULT 0")
    private Double pointsBalance;


    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.referralCode = generateReferralCode();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    private String generateReferralCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder referralCode = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            referralCode.append(characters.charAt(random.nextInt(characters.length())));
        }
        return referralCode.toString();
    }

    public void deductPoints(int points) {
        this.pointsBalance -= points;
        if (this.pointsBalance < 0) {
            this.pointsBalance = (double) 0;
        }
    }
}