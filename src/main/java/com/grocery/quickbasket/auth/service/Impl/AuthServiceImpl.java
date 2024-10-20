package com.grocery.quickbasket.auth.service.Impl;

import com.grocery.quickbasket.auth.dto.PasswordReqDto;
import com.grocery.quickbasket.auth.dto.PayloadSocialLoginReqDto;
import com.grocery.quickbasket.auth.dto.SocialLoginRespDto;
import com.grocery.quickbasket.auth.repository.AuthRedisRepository;
import com.grocery.quickbasket.auth.service.AuthService;
import com.grocery.quickbasket.email.service.EmailService;
import com.grocery.quickbasket.exceptions.DataNotFoundException;
import com.grocery.quickbasket.exceptions.EmailAlreadyExistException;
import com.grocery.quickbasket.exceptions.EmailNotExistException;
import com.grocery.quickbasket.exceptions.PasswordNotMatchException;
import com.grocery.quickbasket.referrals.entity.Referrals;
import com.grocery.quickbasket.referrals.repository.ReferralRepository;
import com.grocery.quickbasket.store.service.StoreAdminService;
import com.grocery.quickbasket.user.dto.RegisterReqDto;
import com.grocery.quickbasket.user.dto.RegisterRespDto;
import com.grocery.quickbasket.user.entity.Role;
import com.grocery.quickbasket.user.entity.User;
import com.grocery.quickbasket.user.repository.UserRepository;
import com.grocery.quickbasket.user.service.UserService;
import com.grocery.quickbasket.vouchers.entity.UserVoucher;
import com.grocery.quickbasket.vouchers.entity.Voucher;
import com.grocery.quickbasket.vouchers.repository.UserVoucherRepository;
import com.grocery.quickbasket.vouchers.repository.VoucherRepository;

import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Log
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthRedisRepository authRedisRepository;
    private final EmailService emailService;
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final UserVoucherRepository userVoucherRepository;
    private final StoreAdminService storeAdminService;

    public AuthServiceImpl(JwtEncoder jwtEncoder, UserService userService, PasswordEncoder passwordEncoder, AuthRedisRepository authRedisRepository, EmailService emailService, ReferralRepository  referralRepository, VoucherRepository voucherRepository, UserRepository userRepository, UserVoucherRepository userVoucherRepository, StoreAdminService storeAdminService) {
        this.jwtEncoder = jwtEncoder;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authRedisRepository = authRedisRepository;
        this.emailService = emailService;
        this.referralRepository = referralRepository;
        this.userRepository = userRepository;
        this.voucherRepository = voucherRepository;
        this.userVoucherRepository = userVoucherRepository;
        this.storeAdminService = storeAdminService;
    }

    @Transactional
    @Override
    public RegisterRespDto register(RegisterReqDto registerReqDto) {
        User existingUser = userService.findByEmail(registerReqDto.getEmail());

        if (existingUser != null) {
            if (userService.isUserSocialLogin(registerReqDto.getEmail())) {
                log.info("EMAIL IS SOCIAL LOGIN" + registerReqDto.getEmail());
                throw new EmailAlreadyExistException("This email is already registered with a social account. Please log in using your social account.");
            }

            // Check if the existing user is verified
            if (existingUser.getIsVerified()) {
                throw new EmailAlreadyExistException("This email is already registered and verified");
            }

            // Update existing unverified user's information
            existingUser.setName(registerReqDto.getName());
            existingUser.setPhone(registerReqDto.getPhone());
            userService.save(existingUser);

            // Resend verification email
            sendVerificationEmail(existingUser.getEmail(), AuthRedisRepository.REGISTRATION_PREFIX, "verify");

            RegisterRespDto respDto = new RegisterRespDto();
            respDto.setEmail(existingUser.getEmail());
            respDto.setName(existingUser.getName());
            return respDto;
        }

        // Create new user if email doesn't exist
        User newUser = new User();
        newUser.setEmail(registerReqDto.getEmail());
        newUser.setName(registerReqDto.getName());
        newUser.setPhone(registerReqDto.getPhone());
        newUser.setIsVerified(false);
        userService.save(newUser);

        if (registerReqDto.getReferralCode() != null) {
            handleNewRegistrationWithReferral(newUser, registerReqDto.getReferralCode());
        }

        sendVerificationEmail(newUser.getEmail(), AuthRedisRepository.REGISTRATION_PREFIX, "verify");

        RegisterRespDto respDto = new RegisterRespDto();
        respDto.setEmail(newUser.getEmail());
        respDto.setName(newUser.getName());
        return respDto;
    }

    @Override
    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        User user = userService.findByEmail(authentication.getName());
        Long userId = user.getId();

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("quick-basket")
                .issuedAt(now)
                .expiresAt(now.plus(12, ChronoUnit.HOURS))
                .subject(authentication.getName())  // Use username as the subject
                .claim("scope", scope)
                .claim("userId", userId);

        // add store_id for store_admin
        if (user.getRole() == Role.store_admin) {
            Long storeId = storeAdminService.getStoreIdForUser(userId);
            claimsBuilder.claim("store_id", storeId);
        }

        JwtClaimsSet claims = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public boolean verifyCode(String code, String prefix) {
        String email = authRedisRepository.getEmail(code, prefix);

        return email != null;
    }

    @Override
    public String addPassword(PasswordReqDto passwordReqDto) {
        String email = authRedisRepository.getEmail(passwordReqDto.getVerificationCode(), AuthRedisRepository.REGISTRATION_PREFIX);

        if (!Objects.equals(passwordReqDto.getPassword(), passwordReqDto.getConfirmPassword())) {
            throw new PasswordNotMatchException("Password not match");
        }
        log.info(passwordReqDto.toString());
        User user = userService.findByEmail(email);
        user.setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        user.setIsVerified(true);
        userService.save(user);

        authRedisRepository.deleteVerificationToken(passwordReqDto.getVerificationCode(), AuthRedisRepository.REGISTRATION_PREFIX);

        return "Password added successfully";
    }

    @Override
    public String generateJwtSocialLogin(PayloadSocialLoginReqDto payloadSocialLoginReqDto) {
        Instant now = Instant.now();
        User user = userService.findByEmail(payloadSocialLoginReqDto.getEmail());

        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("quick-basket")
                .issuedAt(now)
                .expiresAt(now.plus(12, ChronoUnit.HOURS))
                .subject(user.getEmail())
                .claim("scope", user.getRole().name())
                .claim("userId", user.getId());
        // add store_id for store_admin
        if (user.getRole() == Role.store_admin) {
            Long storeId = storeAdminService.getStoreIdForUser(user.getId());
            claimsBuilder.claim("store_id", storeId);
        }

        JwtClaimsSet claims = claimsBuilder.build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

    }

    @Override
    public SocialLoginRespDto googleSignIn(PayloadSocialLoginReqDto payloadSocialLoginReqDto) {
        boolean exist = userService.existsByEmail(payloadSocialLoginReqDto.getEmail());

        SocialLoginRespDto socialLoginRespDto = new SocialLoginRespDto();
        if (exist) {
            String token = generateJwtSocialLogin(payloadSocialLoginReqDto);
            socialLoginRespDto.setToken(token);
            socialLoginRespDto.setStatus("success");
        } else {
            User newUser = userService.saveUserFromSocialLogin(payloadSocialLoginReqDto);
            String token = generateJwtSocialLogin(payloadSocialLoginReqDto);
            socialLoginRespDto.setStatus("new_user");
            socialLoginRespDto.setToken(token);
        }

        return socialLoginRespDto;
    }

    @Override
    public void sendVerificationEmail(String email, String prefix, String linkType) {
        String verificationCode = UUID.randomUUID().toString();
        String verificationLink = "https://quick-basket-fe-dev.netlify.app/" + linkType + "?code=" + verificationCode;

        authRedisRepository.saveVerificationToken(email, verificationCode, prefix);
        emailService.sendEmail(email, verificationLink);
    }

    @Override
    public String checkUserResetPassword(String email) {
        User user = userService.findByEmail(email);

        if (user == null) {
            throw new EmailNotExistException("email not exist");
        }

        sendVerificationEmail(email, AuthRedisRepository.RESET_PREFIX, "reset-password");

        return "Email verification has been send to your email";
    }

    @Override
    public String resetPassword(PasswordReqDto passwordReqDto) {
        String email = authRedisRepository.getEmail(passwordReqDto.getVerificationCode(), AuthRedisRepository.RESET_PREFIX);

        User user = userService.findByEmail(email);
        if (user == null){
            throw new EmailNotExistException("Email not exist");
        }

        if (!Objects.equals(passwordReqDto.getPassword(), passwordReqDto.getConfirmPassword())) {
            throw new PasswordNotMatchException("Password not match");
        }

        user.setPassword(passwordEncoder.encode(passwordReqDto.getPassword()));
        userService.save(user);

        authRedisRepository.deleteVerificationToken(passwordReqDto.getVerificationCode(), AuthRedisRepository.RESET_PREFIX);

        return "Password successfully reset";

    }

    @Transactional
    @Override
    public void handleNewRegistrationWithReferral(User newUser, String referralCode) {
        User referringUser = userRepository.findByReferralCode(referralCode)
                .orElseThrow(() -> new DataNotFoundException("Referring user not found"));

        Referrals discount = new Referrals();
        discount.setUser(newUser);
        discount.setReferringUser(referringUser);
        referralRepository.save(discount);

        Voucher voucherReferrals = voucherRepository.findByCode("REFERRAL")
            .orElseThrow(() -> new DataNotFoundException("Referring referral not found"));; 
         UserVoucher userVoucher = new UserVoucher();
         userVoucher.setUser(newUser);
         userVoucher.setVoucher(voucherReferrals);
         userVoucher.setIsUsed(false);
         userVoucherRepository.save(userVoucher);
    }

    @Override
    public void logout(String token) {
        if (token != null && !token.isEmpty()){
            authRedisRepository.blacklistToken(token);
        }
    }
}

