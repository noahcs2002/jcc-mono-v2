package com.software.teamfive.jcc_product_inventory_management.utility.exception.user;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID userid) {
        super(String.format("User with id %s not found", userid));
    }
}
