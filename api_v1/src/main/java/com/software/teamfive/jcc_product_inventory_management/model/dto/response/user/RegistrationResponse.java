package com.software.teamfive.jcc_product_inventory_management.model.dto.response.user;

import com.software.teamfive.jcc_product_inventory_management.model.biz.User;

import java.util.UUID;

public class RegistrationResponse {

    private String email;
    private String token;
    private UUID userId;
    private UUID companyId;

    public RegistrationResponse(String email, String token, UUID userId, UUID companyId) {
        this.email = email;
        this.token = token;
        this.userId = userId;
        this.companyId = companyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }
}
