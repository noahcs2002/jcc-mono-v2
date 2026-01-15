package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.audit.TransactionAudit;
import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.transaction.CreateTransactionRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.transaction.ArchiveTransactionResponse;
import com.software.teamfive.jcc_product_inventory_management.model.fin.Transaction;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.repo.CompanyRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.TransactionRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.UserRepository;
import com.software.teamfive.jcc_product_inventory_management.utility.config.PermissionKeys;
import com.software.teamfive.jcc_product_inventory_management.utility.config.TransactionAuditStatus;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.transaction.TransactionNotFoundException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.company.CompanyNotFoundException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.join.UserNotInCompanyException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.permission.InsufficientPermissionsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyMemberService companyMemberService;
    private final PermissionValidatorService permissionValidatorService;
    private final TransactionAuditService transactionAuditService;


    @Autowired
    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              CompanyRepository companyRepository,
                              CompanyMemberService companyMemberService,
                              PermissionValidatorService permissionValidatorService,
                              TransactionAuditService transactionAuditService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyMemberService = companyMemberService;
        this.permissionValidatorService = permissionValidatorService;
        this.transactionAuditService = transactionAuditService;
    }

    @Transactional
    public Transaction createTransaction(UUID userId, UUID companyId, CreateTransactionRequest request) {

        Objects.requireNonNull(userId);
        Objects.requireNonNull(companyId);
        Objects.requireNonNull(request);

        // Find user
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Find Company
        Company company = this.companyRepository
                .findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        // Make sure user is in company
        CompanyMember companyMember = this.companyMemberService
                .getCompanyMemberForUserAndCompany(user, company)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        // Permissions Check
        boolean isAuthourised = this.permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.CREATE_TRANSACTION);

        if(!isAuthourised) {
            throw new InsufficientPermissionsException(userId, PermissionKeys.CREATE_TRANSACTION);
        }

        // Record Transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDateOfTransaction(request.getDateOfTransaction());
        transaction.setCreatedBy(user);
        transaction.setCompany(company);
        transaction.setStatus(request.getStatus());
        transaction.setDescription(request.getDescription());

        Transaction result = this.transactionRepository.save(transaction);

        this.transactionAuditService.createTransactionAudit(
                result.getId(),
                result.getCreatedBy().getId(),
                TransactionAuditStatus.CREATED,
                "n/a",
                String.format("Transaction %s created for %f", result.getId(), result.getAmount())
        );

        return result;
    }

    public List<Transaction> getForCompanyAndUser(UUID userId, UUID companyId) {

        Objects.requireNonNull(userId);
        Objects.requireNonNull(companyId);

        // Find user
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Find Company
        Company company = this.companyRepository
                .findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        // Make sure user is in company
        CompanyMember companyMember = this.companyMemberService
                .getCompanyMemberForUserAndCompany(user, company)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        boolean isAuth = this.permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.READ_TRANSACTION);

        if(!isAuth) {
            throw new InsufficientPermissionsException(userId, PermissionKeys.READ_TRANSACTION);
        }

        return this.transactionRepository
                .findAllByDateArchivedIsNullAndDateDeletedIsNullAndCompanyIdAndCreatedById(companyId, userId);
    }

    public Transaction archive(UUID userId, UUID companyId, UUID transactionId) {
        Objects.requireNonNull(transactionId);
        Objects.requireNonNull(companyId);
        Objects.requireNonNull(transactionId);

        // Find user
        User user = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Find Company
        Company company = this.companyRepository
                .findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        // Make sure user is in company
        CompanyMember companyMember = this.companyMemberService
                .getCompanyMemberForUserAndCompany(user, company)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        // Permissions Check
        boolean isAuthourised = this.permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.ARCHIVE_TRANSACTION);

        if(!isAuthourised) {
            throw new InsufficientPermissionsException(userId, PermissionKeys.ARCHIVE_TRANSACTION);
        }
        
        Transaction transaction = this.transactionRepository.findById(transactionId).orElseThrow(
                () -> new TransactionNotFoundException(transactionId)
        );

        transaction.setDateArchived(Instant.now());
        Transaction result = this.transactionRepository.save(transaction);

        this.transactionAuditService.createTransactionAudit(
                result.getId(),
                result.getCreatedBy().getId(),
                TransactionAuditStatus.ARCHIVED,
                "n/a",
                String.format("Transaction %s archived by %s", result.getId(), userId)
        );

        return result;
    }
}
