package com.taskforge.taskforge_core_service.repository;

import com.taskforge.taskforge_core_service.entity.LoginOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginOTPRepository extends JpaRepository<LoginOTP,Long> {

    @Query("SELECT o FROM LoginOTP o WHERE o.user.userId = :userId AND o.expiresAt > :now AND o.isUsed = false")
    List<LoginOTP> findValidOTPsByUser(Long userId, LocalDateTime now);

    @Modifying
    @Query("UPDATE LoginOTP o SET o.isUsed = true WHERE o.user.userId = :userId AND o.isUsed = false")
    void invalidateUserOTPs(Long userId);

    @Modifying
    @Query("DELETE FROM LoginOTP o WHERE o.expiresAt < :now")
    void deleteByExpiresAtBefore(LocalDateTime now);


}
