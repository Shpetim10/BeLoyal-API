package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens", indexes = {
        @Index(name="idx_refresh_user", columnList = "user_id"),
        @Index(name="idx_refresh_hash", columnList = "tokenHash", unique = true)
},
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_refresh_token_token_hash", columnNames = "token_hash")
        })
public class RefreshToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false,fetch = FetchType.LAZY)
  private User user;

  @Column(nullable = false, unique = true, length=64)
  private String tokenHash;

  @Column(nullable=false)
  private LocalDateTime expiresAt;

  private LocalDateTime revokedAt;

  @Column(nullable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  //for mobile
  private String deviceId;
  private String userAgent;

  public boolean isRevoked() {
    return revokedAt != null;
  }

  public boolean isExpired(LocalDateTime now) {
    return !expiresAt.isAfter(LocalDateTime.now());
  }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
