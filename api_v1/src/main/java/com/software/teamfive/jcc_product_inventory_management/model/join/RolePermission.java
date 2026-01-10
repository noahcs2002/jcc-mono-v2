package com.software.teamfive.jcc_product_inventory_management.model.join;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Permission;
import com.software.teamfive.jcc_product_inventory_management.model.biz.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "join_role_permission",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"role_id", "permission_id"})
    }
)
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "permission_id")
    private Permission permission;

    @CreationTimestamp
    private Instant dateCreated;

    private Instant dateArchived;
    private Instant dateDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RolePermission)) return false;
        return id != null && id.equals(((RolePermission) o).id);
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
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

