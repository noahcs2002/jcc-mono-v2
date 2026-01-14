package com.software.teamfive.jcc_product_inventory_management.utility.exception.transaction;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException{
    public TransactionNotFoundException(UUID transactionId) {
       super(String.format("Transaction with id %s not found", transactionId));
    }
}
