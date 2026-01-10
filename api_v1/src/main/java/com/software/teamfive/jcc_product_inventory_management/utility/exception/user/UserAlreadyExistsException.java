package com.software.teamfive.jcc_product_inventory_management.utility.exception.user;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super(String.format("User with email %s already exists", email));
    }
}
