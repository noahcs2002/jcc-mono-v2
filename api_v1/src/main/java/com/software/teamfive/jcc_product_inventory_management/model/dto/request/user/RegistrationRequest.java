package com.software.teamfive.jcc_product_inventory_management.model.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegistrationRequest {

    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String passwordPlainText;

    @NotBlank
    private String companyName;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPasswordPlainText() {
        return passwordPlainText;
    }

    public void setPasswordPlainText(String passwordPlainText) {
        this.passwordPlainText = passwordPlainText;
    }
}
