package com.software.teamfive.jcc_product_inventory_management.utility.exception.company;

import java.util.UUID;

public class CompanyNotFoundException extends RuntimeException {
    public CompanyNotFoundException(UUID companyId) {
        super(String.format("Company %s not found", companyId));
    }
}
