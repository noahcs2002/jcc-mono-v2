package com.software.teamfive.jcc_product_inventory_management.utility.exception.product;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(UUID productId) {
        super("Product with id \"" + productId + "\" not found");
    }
}
