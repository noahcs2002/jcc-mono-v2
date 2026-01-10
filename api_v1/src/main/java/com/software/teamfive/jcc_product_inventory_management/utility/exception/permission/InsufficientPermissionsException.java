package com.software.teamfive.jcc_product_inventory_management.utility.exception.permission;

import com.software.teamfive.jcc_product_inventory_management.utility.config.PermissionKeys;

import java.util.UUID;

public class InsufficientPermissionsException extends RuntimeException {
    public InsufficientPermissionsException(UUID userId, PermissionKeys permissionKey) {
        super(String.format("User does not have the required permission for operation(s):%nUser: %s%nPermission: %s", userId, permissionKey));
    }
}
