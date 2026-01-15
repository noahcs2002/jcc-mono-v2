package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.audit.TransactionAudit;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.fin.Transaction;
import com.software.teamfive.jcc_product_inventory_management.repo.TransactionAuditRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.TransactionRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.UserRepository;
import com.software.teamfive.jcc_product_inventory_management.utility.config.TransactionAuditStatus;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.transaction.TransactionNotFoundException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransactionAuditService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionAuditRepository taRepository;

    @Autowired
    public TransactionAuditService(TransactionRepository transactionRepository,
                                   UserRepository userRepository,
                                   TransactionAuditRepository taRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.taRepository = taRepository;
    }

    public TransactionAudit createTransactionAudit(
            UUID toAudit,
            UUID userId,
            TransactionAuditStatus type,
            String previousValue,
            String nextValue)
    {
        Objects.requireNonNull(toAudit);
        Objects.requireNonNull(userId);
        Objects.requireNonNull(type);
        Objects.requireNonNull(previousValue);
        Objects.requireNonNull(nextValue);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );

        Transaction transaction = transactionRepository.findById(toAudit).orElseThrow(
                () -> new TransactionNotFoundException(toAudit)
        );

        TransactionAudit transactionAudit = new TransactionAudit();
        transactionAudit.setTransaction(transaction);
        transactionAudit.setPerformedBy(user);
        transactionAudit.setType(type);
        transactionAudit.setPreviousValue(previousValue);
        transactionAudit.setNextValue(nextValue);
        transactionAudit.setDate(Instant.now());

        return taRepository.save(transactionAudit);
    }
}
