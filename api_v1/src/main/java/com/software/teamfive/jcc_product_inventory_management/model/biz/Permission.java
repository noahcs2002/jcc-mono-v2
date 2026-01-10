package com.software.teamfive.jcc_product_inventory_management.model.biz;

import com.software.teamfive.jcc_product_inventory_management.model.join.UserRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "biz_permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, updatable = false)
    // e.g. DELETE_TRANSACTION
    private String key;

    @Column(nullable = false)
    // e.g. Can delete transactions
    private String description;

    @Column(nullable = false)
    // e.g. TRANSACTION
    private String domain;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant dateCreated;

    private Instant dateArchived;
    private Instant dateDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Permission)) return false;
        return id != null && id.equals(((Permission) o).id);
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

