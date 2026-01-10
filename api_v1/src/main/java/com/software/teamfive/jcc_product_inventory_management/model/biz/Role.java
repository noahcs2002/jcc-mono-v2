package com.software.teamfive.jcc_product_inventory_management.model.biz;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "biz_role",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"company_id", "name"})
    }
)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @CreationTimestamp
    private Instant dateCreated;

    private Instant dateArchived;

    private Instant dateDeleted;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        return id != null && id.equals(((Role) o).id);
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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