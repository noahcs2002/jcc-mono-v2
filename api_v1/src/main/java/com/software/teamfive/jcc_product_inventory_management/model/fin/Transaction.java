package com.software.teamfive.jcc_product_inventory_management.model.fin;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.enums.TransactionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "fin_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "created_by_user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;

    @JoinColumn(name = "company", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @NotBlank
    @NotNull
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus status;

    // Differs from dateCreated - dateCreated is for internal metadata and auditing, this is customer facing.
    private Instant dateOfTransaction;

    @Column(length = 200)
    private String description;

    @CreationTimestamp
    private Instant dateCreated;
    private Instant dateArchived;
    private Instant dateDeleted;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Instant getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(Instant dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
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

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
