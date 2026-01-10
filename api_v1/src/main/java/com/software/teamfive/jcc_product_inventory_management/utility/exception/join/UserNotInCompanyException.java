package com.software.teamfive.jcc_product_inventory_management.utility.exception.join;

import java.util.UUID;

public class UserNotInCompanyException extends RuntimeException {
    public UserNotInCompanyException(UUID userId, UUID companyId) {
        super(String.format("User %s not found in company %s", userId, companyId));
    }
}
