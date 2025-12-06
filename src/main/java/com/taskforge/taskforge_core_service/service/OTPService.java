package com.taskforge.taskforge_core_service.service;

import com.taskforge.taskforge_core_service.entity.LoginOTP;
import com.taskforge.taskforge_core_service.entity.User;
import com.taskforge.taskforge_core_service.repository.LoginOTPRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class OTPService {

    private final LoginOTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final Executor taskExecutor;

    public OTPService(LoginOTPRepository otpRepository, PasswordEncoder passwordEncoder, Executor taskExecutor) {
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskExecutor = taskExecutor;
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generateOtp(User user) {
        return CompletableFuture.supplyAsync(() -> {

            otpRepository.invalidateUserOTPs(user.getUserId());

            SecureRandom random = new SecureRandom();
            int otp = 100000 + random.nextInt(900000);
            String otpString = String.valueOf(otp);

            LoginOTP loginOTP = LoginOTP.builder()
                    .user(user)
                    .otpHash(passwordEncoder.encode(otpString))
                    .expiresAt(LocalDateTime.now().plusMinutes(10))
                    .build();

            otpRepository.save(loginOTP);

            log.info("OTP generated for user: {}", user.getEmail());

            return otpString;

        }, taskExecutor);
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> verifyOtp(User user, String code) {
        return CompletableFuture.supplyAsync(() -> {
            List<LoginOTP> otps = otpRepository.findValidOTPsByUser(
                    user.getUserId(),
                    LocalDateTime.now()
            );

            if (otps.isEmpty()) {
                log.warn("No valid OTP found for user: {}", user.getEmail());
                return false;
            }

            return otps.stream()
                    .filter(otp -> !otp.getIsUsed())
                    .filter(otp -> otp.getAttempts() < 3)
                    .anyMatch(otp -> {
                        if (passwordEncoder.matches(code, otp.getOtpHash())) {
                            otp.setIsUsed(true);
                            otpRepository.save(otp);
                            log.info("OTP verified successfully for user: {}", user.getEmail());
                            return true;
                        } else {
                            otp.setAttempts(otp.getAttempts() + 1);
                            otpRepository.save(otp);
                            log.warn("Invalid OTP attempt for user: {}", user.getEmail());
                            return false;
                        }
                    });
        }, taskExecutor);

    }
}
