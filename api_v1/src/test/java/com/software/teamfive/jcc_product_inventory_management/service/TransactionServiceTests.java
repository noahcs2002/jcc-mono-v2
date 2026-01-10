package com.software.teamfive.jcc_product_inventory_management.service;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @InjectMocks
    private TransactionService transactionService;


    @Test
    void givenRequestAndUserAndCompany_whenAllIsValid_TransactionIsSaved() {

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


        // Act
        Transaction result = this.transactionService.createTransaction(UUID.randomUUID(), UUID.randomUUID(), request);

        // Assert
        verify(companyRepository, times(1)).findById(any(UUID.class));
        verify(userRepository, times(1)).findById(any(UUID.class));
        verify(companyMemberService, times(1)).getCompanyMemberForUserAndCompany(any(User.class), any(Company.class));

        assertEquals(result.getCompany().getId(), companyId);
        assertEquals(result.getCreatedBy().getId(), userId);
        assertEquals(result.getAmount(), BigDecimal.valueOf(123.45));
        assertEquals(TransactionStatus.VOID, result.getStatus());
        assertEquals(result.getDateOfTransaction(), Instant.ofEpochMilli(123L));
        assertEquals("Description", result.getDescription());
    }

}
