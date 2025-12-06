package com.taskforge.taskforge_core_service.entity;

import com.taskforge.taskforge_core_service.enums.AuthProvider;
import com.taskforge.taskforge_core_service.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE , generator = "user_seq")
    @SequenceGenerator(
            name = "user_seq",
            sequenceName = "user_seq",
            allocationSize = 50
    )
    private Long userId;

    @Column(length = 120)
    private String firstName;

    @Column(length = 120)
    private String lastName;

    @Column(nullable = false, unique = true , length = 180)
    private String email;

    @Column(length = 180)
    private String passwordHash;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private AuthProvider authProvider= AuthProvider.LOCAL;

    @Column(name = "oauth_provider_id")
    private String oauthProviderId; // Store OAuth provider ID

    private String profilePicture; // Store profile picture URL

    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive=true;

    @Column(nullable = false)
    private Boolean emailVerified;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private UserRole role = UserRole.USER;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
