package com.software.teamfive.jcc_product_inventory_management.utility.exception.product;

public class ProductAlreadyExistsException extends RuntimeException {
    public ProductAlreadyExistsException(String sku) {
        super(String.format("PRODUCT WITH SKU: \"%S\" ALREADY EXISTS", sku));
    }
}
