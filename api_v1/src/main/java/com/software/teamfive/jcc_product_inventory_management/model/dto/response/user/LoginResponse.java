package com.software.teamfive.jcc_product_inventory_management.model.dto.response.user;

import java.util.UUID;

public class LoginResponse {

    private UUID userId;
    private String email;
    private UUID companyId;
    private String token;

    public LoginResponse(UUID userId, String email, UUID companyId, String token) {
        this.userId = userId;
        this.email = email;
        this.companyId = companyId;
        this.token = token;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getCompanyId() {
        return companyId;
    }

    public void setCompanyId(UUID companyId) {
        this.companyId = companyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
