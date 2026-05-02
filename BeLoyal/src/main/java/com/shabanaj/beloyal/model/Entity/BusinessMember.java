package com.shabanaj.beloyal.model.Entity;

import com.shabanaj.beloyal.model.Enums.Role;
import com.shabanaj.beloyal.model.Enums.UserStatus;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "business_members",
        uniqueConstraints = @UniqueConstraint(name = "uk_business_members_user_business", columnNames = {"user_id", "business_id"})
)
@EntityListeners(AuditingEntityListener.class)
public class BusinessMember {

    public enum MemberStatus {
        ACTIVE("Active"),
        INACTIVE("Inactive"),
        INVITE("Invited");

        final String name;

        MemberStatus(String name) {
            this.name = name;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // RESTAURANT_ADMIN or STAFF

    @Enumerated(EnumType.STRING)
    @Column(name = "member_status", nullable = false)
    private MemberStatus memberStatus = MemberStatus.INVITE;

    @Column(name = "hired_at")
    private LocalDate hiredAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public BusinessMember() {}

    public BusinessMember(User user, Business business, Role role) {
        this.user = user;
        this.business = business;
        this.role = role;
        this.memberStatus = MemberStatus.ACTIVE;
    }

    // Domain helpers
    public void activate(){
        this.memberStatus = MemberStatus.ACTIVE;
        if(this.role==Role.BUSINESS_ADMIN){
            this.user.setStatus(UserStatus.ENABLED);
        }
    }

    public void deactivate(){
        this.memberStatus = MemberStatus.INACTIVE;
    }
    // Getters/Setters
    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Business getBusiness() { return business; }
    public void setBusiness(Business business) { this.business = business; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public MemberStatus getMemberStatus() { return memberStatus; }
    public void setMemberStatus(MemberStatus memberStatus) { this.memberStatus = memberStatus; }

    public LocalDate getHiredAt() { return hiredAt; }
    public void setHiredAt(LocalDate hiredAt) { this.hiredAt = hiredAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
