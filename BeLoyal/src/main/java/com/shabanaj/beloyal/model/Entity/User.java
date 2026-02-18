package com.shabanaj.beloyal.model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import com.shabanaj.beloyal.common.Validation.Annotation.UniqueEmailOnCreate;
import com.shabanaj.beloyal.common.Validation.Annotation.UniqueUsernameOnCreate;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank
    @UniqueUsernameOnCreate
    private String username;

    @Column(unique = true, nullable = false, length = 255)
    @NotBlank
    @Email
    @UniqueEmailOnCreate
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    @JsonIgnore
    private String passwordHash;

    @Column(nullable = true, unique = true, length = 30)
    private String phoneNumber;

    @Column(name = "profile_image")
    private String profileImage;

    // Global roles only (PLATFORM_ADMIN). Business roles are stored in BusinessMember.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(nullable = false)
    private boolean emailVerified = false;

    private LocalDateTime emailVerifiedAt;

    private LocalDateTime lastLoginAt;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    private LocalDateTime lockedUntil;

    // T&C tracking (store what the user accepted)
    @Column(name = "accepted_tc_version", length = 50)
    private String acceptedTcVersion;

    private LocalDateTime acceptedTcAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public User() {
        //
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (email != null) email = email.trim().toLowerCase();
        if (username != null) username = username.trim().toLowerCase();
    }

    // Domain Methods
    public void unlock() {
        this.status = UserStatus.ENABLED;
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public boolean isLockActive(LocalDateTime now) {
        return this.status == UserStatus.LOCKED
                && this.lockedUntil != null
                && this.lockedUntil.isAfter(now);
    }

    public void recordFailedLogin(LocalDateTime now, int maxAttempts, int lockMinutes) {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= maxAttempts) {
            this.status = UserStatus.LOCKED;
            this.lockedUntil = now.plusMinutes(lockMinutes);
        }
    }

    public void recordSuccessfulLogin(LocalDateTime now) {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.lastLoginAt = now;
    }

    // Getters/Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public LocalDateTime getEmailVerifiedAt() { return emailVerifiedAt; }
    public void setEmailVerifiedAt(LocalDateTime emailVerifiedAt) { this.emailVerifiedAt = emailVerifiedAt; }

    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(LocalDateTime lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public Integer getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(Integer failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }

    public String getAcceptedTcVersion() { return acceptedTcVersion; }
    public void setAcceptedTcVersion(String acceptedTcVersion) { this.acceptedTcVersion = acceptedTcVersion; }

    public LocalDateTime getAcceptedTcAt() { return acceptedTcAt; }
    public void setAcceptedTcAt(LocalDateTime acceptedTcAt) { this.acceptedTcAt = acceptedTcAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
