package com.software.teamfive.jcc_product_inventory_management.model.biz;

import com.software.teamfive.jcc_product_inventory_management.model.enums.CompanyStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "biz_company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String name;

    @CreationTimestamp
    private Instant dateCreated;

    private Instant dateArchived;

    private Instant dateDeleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company)) return false;
        return id != null && id.equals(((Company) o).id);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }
}
