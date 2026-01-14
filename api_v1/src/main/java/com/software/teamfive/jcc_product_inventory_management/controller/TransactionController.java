package com.software.teamfive.jcc_product_inventory_management.controller;

import com.software.teamfive.jcc_product_inventory_management.model.dto.request.transaction.CreateTransactionRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.transaction.ArchiveTransactionResponse;
import com.software.teamfive.jcc_product_inventory_management.model.fin.Transaction;
import com.software.teamfive.jcc_product_inventory_management.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jcc/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/{userId}/{companyId}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public ResponseEntity<Transaction> createTransaction(@PathVariable UUID userId,
                                                         @PathVariable UUID companyId,
                                                         @RequestBody CreateTransactionRequest request) {

        final Transaction result = this.transactionService.createTransaction(userId, companyId, request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}/{companyId}")
    public ResponseEntity<List<Transaction>> getAllForCompanyAndUser(@PathVariable UUID userId, @PathVariable UUID companyId) {
        final List<Transaction> validTransactions = this.transactionService.getForCompanyAndUser(userId, companyId);
        return ResponseEntity.ok(validTransactions);
    }

    @DeleteMapping("/{userId}/{companyId}/{transactionId}")
    public ResponseEntity<ArchiveTransactionResponse> archiveTransaction(@PathVariable UUID userId, @PathVariable UUID companyId, @PathVariable UUID transactionId) {
        Transaction response = this.transactionService.archive(userId, companyId, transactionId);

        ArchiveTransactionResponse archiveTransactionResponse = new ArchiveTransactionResponse();
        archiveTransactionResponse.setArchivedAt(response.getDateArchived());
        archiveTransactionResponse.setUpdatedTransaction(response);
        archiveTransactionResponse.setSuccessful(true);

        return ResponseEntity.ok(archiveTransactionResponse);
    }
}
