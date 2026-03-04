package com.shabanaj.beloyal.model.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@Table(name = "reset_password_token",
uniqueConstraints = {
        @UniqueConstraint(name = "unique_reset_token_constraint", columnNames = "token")
})
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ResetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @JoinColumn(unique = true, nullable = false, name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column
    private boolean used;

    @CreatedDate
    private LocalDateTime createdDate;
}
