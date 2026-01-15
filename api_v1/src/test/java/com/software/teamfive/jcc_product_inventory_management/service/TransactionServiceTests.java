package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.audit.TransactionAudit;
import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.transaction.CreateTransactionRequest;
import com.software.teamfive.jcc_product_inventory_management.model.enums.TransactionStatus;
import com.software.teamfive.jcc_product_inventory_management.model.fin.Transaction;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.repo.CompanyMemberRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.CompanyRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.TransactionRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.UserRepository;
import com.software.teamfive.jcc_product_inventory_management.utility.config.PermissionKeys;
import com.software.teamfive.jcc_product_inventory_management.utility.config.TransactionAuditStatus;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.permission.InsufficientPermissionsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.parameters.P;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMemberRepository companyMemberRepository;

    @Mock
    private CompanyMemberService companyMemberService;

    @Mock
    private PermissionValidatorService permissionValidatorService;

    @Mock
    private TransactionAuditService transactionAuditService;

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void saveTransaction_IdealSystemTest() {

        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID companyId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Company company = new Company();
        company.setId(companyId);

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(memberId);

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(123.45));
        request.setStatus(TransactionStatus.VOID);
        request.setDateOfTransaction(Instant.ofEpochMilli(123L));
        request.setDescription("Description");

        Transaction transaction = new Transaction();
        transaction.setCompany(company);
        transaction.setCreatedBy(user);
        transaction.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transaction.setDescription("Description");
        transaction.setStatus(TransactionStatus.VOID);
        transaction.setAmount(BigDecimal.valueOf(123.45));

        when(this.transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(this.companyMemberService.getCompanyMemberForUserAndCompany(any(User.class), any(Company.class))).thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), any(PermissionKeys.class))).thenReturn(true);
        when(this.transactionAuditService.createTransactionAudit(any(), any(), any(), any(), any())).thenReturn(new TransactionAudit());

        // Act
        Transaction result = this.transactionService.createTransaction(UUID.randomUUID(), UUID.randomUUID(), request);

        // Assert
        verify(companyRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(companyMemberService, times(1)).getCompanyMemberForUserAndCompany(any(User.class), any(Company.class));
        verify(transactionAuditService, times(1)).createTransactionAudit(any(), any(), eq(TransactionAuditStatus.CREATED), any(), any());
        verify(permissionValidatorService, times(1)).doesUserHavePerm(any(CompanyMember.class), eq(PermissionKeys.CREATE_TRANSACTION));

        assertEquals(result.getCompany().getId(), companyId);
        assertEquals(result.getCreatedBy().getId(), userId);
        assertEquals(result.getAmount(), BigDecimal.valueOf(123.45));
        assertEquals(TransactionStatus.VOID, result.getStatus());
        assertEquals(result.getDateOfTransaction(), Instant.ofEpochMilli(123L));
        assertEquals("Description", result.getDescription());
    }

    @Test()
    void saveTransaction_throwsOnNullCompanyId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> this.transactionService.createTransaction(null, UUID.randomUUID(), new CreateTransactionRequest()));
    }

    @Test()
    void saveTransaction_throwsOnNullUserId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> this.transactionService.createTransaction(UUID.randomUUID(), null, new CreateTransactionRequest()));
    }

    @Test()
    void saveTransaction_throwsOnNullRequest() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> this.transactionService.createTransaction(UUID.randomUUID(), UUID.randomUUID(), null));
    }

    @Test
    void saveTransaction_noAuth() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID companyId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Company company = new Company();
        company.setId(companyId);

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(memberId);

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(123.45));
        request.setStatus(TransactionStatus.VOID);
        request.setDateOfTransaction(Instant.ofEpochMilli(123L));
        request.setDescription("Description");

        Transaction transaction = new Transaction();
        transaction.setCompany(company);
        transaction.setCreatedBy(user);
        transaction.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transaction.setDescription("Description");
        transaction.setStatus(TransactionStatus.VOID);
        transaction.setAmount(BigDecimal.valueOf(123.45));

        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(this.companyMemberService.getCompanyMemberForUserAndCompany(any(User.class), any(Company.class))).thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), any(PermissionKeys.class))).thenReturn(false);

        // Assert
        InsufficientPermissionsException exception = assertThrows(InsufficientPermissionsException.class, () -> {

            // Act
            this.transactionService.createTransaction(UUID.randomUUID(), UUID.randomUUID(), new CreateTransactionRequest());
        });
    }

    @Test
    void getForCompanyAndUser_idealTest() {
        final UUID userId = UUID.randomUUID();
        final UUID companyId = UUID.randomUUID();

        final User user = new User();
        user.setId(userId);

        final Company company = new Company();
        company.setId(companyId);

        final CompanyMember companyMember = new CompanyMember();
        companyMember.setUser(user);
        companyMember.setCompany(company);

        final Transaction transactionOne = new Transaction();
        transactionOne.setCompany(company);
        transactionOne.setCreatedBy(user);
        transactionOne.setDateOfTransaction(Instant.ofEpochMilli(123L));

        final Transaction transactionTwo = new Transaction();
        transactionTwo.setCompany(company);
        transactionTwo.setCreatedBy(user);
        transactionTwo.setDateOfTransaction(Instant.ofEpochMilli(456L));

        final List<Transaction> transactions = List.of(transactionOne, transactionTwo);

        when(this.userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(this.companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(this.companyMemberService.getCompanyMemberForUserAndCompany(any(User.class), any(Company.class)))
                .thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), any(PermissionKeys.class)))
                .thenReturn(true);

        when(this.transactionRepository.findAllByDateArchivedIsNullAndDateDeletedIsNullAndCompanyIdAndCreatedById(any(UUID.class), any(UUID.class)))
            .thenReturn(transactions);

        List<Transaction> result = this.transactionService.getForCompanyAndUser(userId, companyId);

        assertEquals(transactions, result);
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(companyRepository, times(1)).findById(any(UUID.class));
        verify(companyMemberService, times(1)).getCompanyMemberForUserAndCompany(any(User.class), any(Company.class));
    }

    @Test()
    void getForCompanyAndUser_noAuth() {

        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID companyId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Company company = new Company();
        company.setId(companyId);

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(memberId);

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(123.45));
        request.setStatus(TransactionStatus.VOID);
        request.setDateOfTransaction(Instant.ofEpochMilli(123L));
        request.setDescription("Description");

        Transaction transaction = new Transaction();
        transaction.setCompany(company);
        transaction.setCreatedBy(user);
        transaction.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transaction.setDescription("Description");
        transaction.setStatus(TransactionStatus.VOID);
        transaction.setAmount(BigDecimal.valueOf(123.45));

        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(this.companyMemberService.getCompanyMemberForUserAndCompany(any(User.class), any(Company.class))).thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), any(PermissionKeys.class))).thenReturn(false);

        InsufficientPermissionsException exception = assertThrows(InsufficientPermissionsException.class, () -> {
           this.transactionService.getForCompanyAndUser(UUID.randomUUID(), UUID.randomUUID());
        });
    }

    @Test()
    void getForCompanyAndUser_throwsOnNullCompanyId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {this.transactionService.getForCompanyAndUser(UUID.randomUUID(), null);});
    }

    @Test()
    void getForCompanyAndUser_throwsOnNullUserId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {this.transactionService.getForCompanyAndUser(null, UUID.randomUUID());});
    }

    @Test()
    void archiveTransaction_idealTest() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID companyId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Company company = new Company();
        company.setId(companyId);

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(memberId);

        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAmount(BigDecimal.valueOf(123.45));
        request.setStatus(TransactionStatus.VOID);
        request.setDateOfTransaction(Instant.ofEpochMilli(123L));
        request.setDescription("Description");

        Transaction transaction = new Transaction();
        transaction.setCompany(company);
        transaction.setCreatedBy(user);
        transaction.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transaction.setDescription("Description");
        transaction.setStatus(TransactionStatus.VOID);
        transaction.setAmount(BigDecimal.valueOf(123.45));

        Transaction transactionArchived = new Transaction();
        transactionArchived.setCompany(company);
        transactionArchived.setCreatedBy(user);
        transactionArchived.setCreatedBy(user);
        transactionArchived.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transactionArchived.setDescription("Description");
        transactionArchived.setStatus(TransactionStatus.VOID);
        transactionArchived.setAmount(BigDecimal.valueOf(123.45));
        transactionArchived.setDateArchived(Instant.ofEpochMilli(123L));

        when(this.transactionRepository.save(any(Transaction.class))).thenReturn(transactionArchived);
        when(this.transactionRepository.findById(any(UUID.class))).thenReturn(Optional.of(transaction));
        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(this.companyMemberService.getCompanyMemberForUserAndCompany(any(User.class), any(Company.class))).thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), any(PermissionKeys.class))).thenReturn(true);
        when(this.transactionAuditService.createTransactionAudit(any(), any(), any(), any(), any())).thenReturn(new TransactionAudit());

        Transaction result = this.transactionService.archive(userId, companyId, UUID.randomUUID());

        assertEquals(transactionArchived, result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(companyRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(1)).findById(any(UUID.class));
    }

    @Test()
    void archiveTransaction_noAuth() {
        // Arrange
        final UUID userId = UUID.randomUUID();
        final UUID companyId = UUID.randomUUID();
        final UUID memberId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Company company = new Company();
        company.setId(companyId);

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(memberId);

        Transaction transaction = new Transaction();
        transaction.setCompany(company);
        transaction.setCreatedBy(user);
        transaction.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transaction.setDescription("Description");
        transaction.setStatus(TransactionStatus.VOID);
        transaction.setAmount(BigDecimal.valueOf(123.45));

        Transaction transactionArchived = new Transaction();
        transaction.setCompany(company);
        transaction.setCreatedBy(user);
        transaction.setDateOfTransaction(Instant.ofEpochMilli(123L));
        transaction.setDescription("Description");
        transaction.setStatus(TransactionStatus.VOID);
        transaction.setAmount(BigDecimal.valueOf(123.45));
        transactionArchived.setDateArchived(Instant.ofEpochMilli(123L));

        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(this.companyMemberService.getCompanyMemberForUserAndCompany(any(User.class), any(Company.class))).thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), eq(PermissionKeys.ARCHIVE_TRANSACTION))).thenReturn(false);

        InsufficientPermissionsException exception = assertThrows(InsufficientPermissionsException.class, () -> {
            this.transactionService.archive(userId, companyId, UUID.randomUUID());
        });
    }

    @Test()
    void archiveTransaction_throwsOnNullTransactionId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.transactionService.archive(UUID.randomUUID(), UUID.randomUUID(), null);
        });
    }

    @Test()
    void archiveTransaction_throwsOnNullUserId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.transactionService.archive(null, UUID.randomUUID(), null);
        });
    }

    @Test()
    void archiveTransaction_throwsOnNullCompanyId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.transactionService.archive(UUID.randomUUID(), UUID.randomUUID(), null);
        });
    }
}
