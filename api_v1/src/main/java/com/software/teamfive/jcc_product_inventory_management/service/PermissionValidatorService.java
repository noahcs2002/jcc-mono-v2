package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Permission;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.model.join.RolePermission;
import com.software.teamfive.jcc_product_inventory_management.model.join.UserRole;
import com.software.teamfive.jcc_product_inventory_management.repo.PermissionRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.RolePermissionRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.RoleRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.UserRoleRepository;
import com.software.teamfive.jcc_product_inventory_management.utility.config.PermissionKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Used to validate that users can perform what they're after
 */
@Service
public class PermissionValidatorService {

    private UserRoleRepository userRoleRepository;
    private RolePermissionRepository rolePermissionRepository;
    private PermissionRepository permissionRepository;

    @Autowired
    public PermissionValidatorService(UserRoleRepository userRoleRepository, RolePermissionRepository rolePermissionRepository, PermissionRepository permissionRepository) {
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean doesUserHavePerm(CompanyMember member, PermissionKeys permissionKey) {
        List<UserRole> userRoles = this.userRoleRepository
                .findByMemberId(member.getId())
                .stream()
                .toList();

        Permission permission = this.permissionRepository.findByKey(permissionKey.name());

        for(final UserRole role : userRoles) {
            List<Permission> permissions = this.rolePermissionRepository.findAllByRole(role.getRole())
                    .stream()
                    .map(RolePermission::getPermission)
                    .toList();

            if (permissions.contains(permission)) {
                return true;
            }
        }

        return false;
    }
}
