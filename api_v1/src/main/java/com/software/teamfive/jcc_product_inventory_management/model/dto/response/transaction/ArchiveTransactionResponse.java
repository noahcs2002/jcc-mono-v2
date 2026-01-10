package com.software.teamfive.jcc_product_inventory_management.model.dto.response.transaction;

import com.software.teamfive.jcc_product_inventory_management.model.fin.Transaction;

import java.time.Instant;

public class ArchiveTransactionResponse {

    private boolean isSuccessful;
    private Instant archivedAt;
    private Transaction updatedTransaction;

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public Instant getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(Instant archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Transaction getUpdatedTransaction() {
        return updatedTransaction;
    }

    public void setUpdatedTransaction(Transaction updatedTransaction) {
        this.updatedTransaction = updatedTransaction;
    }
}
