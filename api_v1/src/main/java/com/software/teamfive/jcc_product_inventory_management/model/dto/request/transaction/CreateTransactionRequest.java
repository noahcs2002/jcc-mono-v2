package com.software.teamfive.jcc_product_inventory_management.model.dto.request.transaction;

import com.software.teamfive.jcc_product_inventory_management.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class CreateTransactionRequest {

    private BigDecimal amount;
    private TransactionStatus status;
    private Instant dateOfTransaction;
    private String description;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Instant getDateOfTransaction() {
        return dateOfTransaction;
    }

    public void setDateOfTransaction(Instant dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
