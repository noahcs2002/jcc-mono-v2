package com.software.teamfive.jcc_product_inventory_management.model.dto.request.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Used in PUT requests to update products
 */
public class UpdateProductPutRequest {

    @NotBlank
    private String sku;

    @NotBlank
    private String description;

    @NotBlank
    private String name;

    @Min(0)
    private Integer quantityInStock;

    @DecimalMin("0.0")
    private Double currentPrice;

    @DecimalMin("0.0")
    private Double salePrice;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }
}
