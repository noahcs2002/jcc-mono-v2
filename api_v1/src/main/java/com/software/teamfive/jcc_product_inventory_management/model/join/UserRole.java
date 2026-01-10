package com.software.teamfive.jcc_product_inventory_management.model.join;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "join_user_company_role",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_member_id", "role_id"})
    },
    indexes = {
        @Index(name = "idx_user_role_member", columnList = "company_member_id"),
        @Index(name = "idx_user_role_role", columnList = "role_id")
    }
)
public class UserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(nullable = false, name = "company_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private CompanyMember member;

    @JoinColumn(nullable = false, name = "role_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Role role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    private Instant dateArchived;

    private Instant dateDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;
        return id != null && id.equals(((UserRole) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CompanyMember getMember() {
        return member;
    }

    public void setMember(CompanyMember member) {
        this.member = member;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Instant getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(Instant dateArchived) {
        this.dateArchived = dateArchived;
    }

    public Instant getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Instant dateDeleted) {
        this.dateDeleted = dateDeleted;
    }
}
