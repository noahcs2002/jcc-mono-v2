package com.software.teamfive.jcc_product_inventory_management.model.dto.request.product;

import jakarta.validation.constraints.*;

import java.util.UUID;

/**
 * Used in POST requests to create products
 */
public class CreateProductRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @Min(0)
    private Integer quantityInStock;

    @NotNull
    @DecimalMin("0.0")
    private Double currentPrice;

    @NotNull
    private UUID createdByUserId;

    @NotNull
    private UUID companyId;

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
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

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
