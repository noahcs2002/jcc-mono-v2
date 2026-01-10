package com.software.teamfive.jcc_product_inventory_management.model.inv;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table( name = "inv_Product",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sku"})
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "sku", nullable = false, unique = true)
    @Size(max = 50)
    private String sku;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer quantityInStock;

    private Double previousPrice;

    @Column(nullable = false)
    private Double currentPrice;
    private Double nextPrice;
    private Double salePrice;

    private Instant dateDeleted;

    private Instant dateArchived;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant dateCreated;

    public Product() {
        // Hibernate constructor
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User user) {
        this.createdByUser = user;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public Integer getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(Integer quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Double getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(Double previousPrice) {
        this.previousPrice = previousPrice;
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Double getNextPrice() {
        return nextPrice;
    }

    public void setNextPrice(Double nextPrice) {
        this.nextPrice = nextPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public Instant getDateDeleted() {
        return dateDeleted;
    }

    public void setDateDeleted(Instant dateDeleted) {
        this.dateDeleted = dateDeleted;
    }

    public Instant getDateArchived() {
        return dateArchived;
    }

    public void setDateArchived(Instant dateArchived) {
        this.dateArchived = dateArchived;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Product product)) return false;
        return Objects.equals(id, product.id) && Objects.equals(createdByUser, product.createdByUser) && Objects.equals(company, product.company) && Objects.equals(sku, product.sku) && Objects.equals(name, product.name) && Objects.equals(description, product.description) && Objects.equals(quantityInStock, product.quantityInStock) && Objects.equals(previousPrice, product.previousPrice) && Objects.equals(currentPrice, product.currentPrice) && Objects.equals(nextPrice, product.nextPrice) && Objects.equals(salePrice, product.salePrice) && Objects.equals(dateDeleted, product.dateDeleted) && Objects.equals(dateArchived, product.dateArchived) && Objects.equals(dateCreated, product.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdByUser, company, sku, name, description, quantityInStock, previousPrice, currentPrice, nextPrice, salePrice, dateDeleted, dateArchived, dateCreated);
    }
}