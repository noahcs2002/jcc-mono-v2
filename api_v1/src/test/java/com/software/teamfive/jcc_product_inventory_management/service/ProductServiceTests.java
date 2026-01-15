package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.product.CreateProductRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.product.UpdateProductPutRequest;
import com.software.teamfive.jcc_product_inventory_management.model.inv.Product;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.repo.CompanyMemberRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.CompanyRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.ProductRepository;
import com.software.teamfive.jcc_product_inventory_management.repo.UserRepository;
import com.software.teamfive.jcc_product_inventory_management.utility.config.PermissionKeys;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.company.CompanyNotFoundException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.join.UserNotInCompanyException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.permission.InsufficientPermissionsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.product.ProductAlreadyExistsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.product.ProductNotFoundException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTests {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMemberRepository companyMemberRepository;

    @Mock
    private PermissionValidatorService permissionValidatorService;

    @InjectMocks
    private ProductService productService;

    private CreateProductRequest request;

    private User user;
    private Company company;
    private CompanyMember membership;

    @BeforeEach
    void setUp() {
        request = new CreateProductRequest();
        request.setSku("SKU-123");
        request.setName("Test Product");
        request.setDescription("Description");
        request.setQuantityInStock(10);
        request.setCurrentPrice(9.99d);
        request.setCreatedByUserId(UUID.randomUUID());
        request.setCompanyId(UUID.randomUUID());

        user = new User();
        user.setId(UUID.randomUUID());

        company = new Company();
        company.setId(UUID.randomUUID());

        membership = new CompanyMember();
        membership.setUser(user);
        membership.setCompany(company);
    }

    private UpdateProductPutRequest updateRequest;
    private Product existingProduct;

    @BeforeEach
    void setUpUpdateProduct() {
        updateRequest = new UpdateProductPutRequest();
        updateRequest.setSku("NEW-SKU");
        updateRequest.setName("Updated Name");
        updateRequest.setDescription("Updated Description");
        updateRequest.setQuantityInStock(20);
        updateRequest.setCurrentPrice(19.99);
        updateRequest.setSalePrice(17.99);

        existingProduct = new Product();
        existingProduct.setId(UUID.randomUUID());
        existingProduct.setSku("OLD-SKU");
        existingProduct.setName("Old Name");
        existingProduct.setDescription("Old Description");
        existingProduct.setQuantityInStock(5);
        existingProduct.setCurrentPrice(9.99d);
        existingProduct.setPreviousPrice(9.99d);
        existingProduct.setNextPrice(9.99d);
    }

    private Product product;

    @BeforeEach
    void setUpArchiveProduct() {
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setDateArchived(null);
    }

    @BeforeEach
    void setUpDeleteProduct() {
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setDateDeleted(null);
        product.setDateArchived(null);
    }

    @BeforeEach
    void setUpUnarchiveProduct() {
        product = new Product();
        product.setId(UUID.randomUUID());
        product.setDateDeleted(null);
    }

    @Test
    void createNew_throwsProductAlreadyExists_whenSkuExists() {
        when(productRepository.existsBySku("SKU-123")).thenReturn(true);

        assertThrows(
                ProductAlreadyExistsException.class,
                () -> productService.createNew(request)
        );

        Mockito.verify(productRepository, never()).save(any());
    }

    @Test
    void createNew_throwsUserNotFound_whenUserDoesNotExist() {
        when(productRepository.existsBySku(any())).thenReturn(false);
        when(userRepository.findById(UUID.randomUUID())).thenReturn(Optional.empty());

        assertThrows(
                Exception.class,
                () -> productService.createNew(request)
        );
    }

    @Test
    void createNew_throwsCompanyNotFound_whenCompanyDoesNotExist() {
        when(productRepository.existsBySku(any())).thenReturn(false);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> productService.createNew(request)
        );
    }

    @Test
    void createNew_throwsRuntimeException_whenUserNotCompanyMember() {
        when(productRepository.existsBySku(any())).thenReturn(false);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> productService.createNew(request)
        );

        assertEquals("User is not a member of the specified company", ex.getMessage());
    }

    @Test
    void createNew_throwsInsufficientPermissions_whenUserLacksPermission() {
        when(productRepository.existsBySku(any())).thenReturn(false);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.CREATE_PRODUCT))
                .thenReturn(false);

        assertThrows(
                InsufficientPermissionsException.class,
                () -> productService.createNew(request)
        );
    }

    @Test
    void createNew_createsAndSavesProduct_whenAllChecksPass() {
        when(productRepository.existsBySku(any())).thenReturn(false);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(companyRepository.findById(any(UUID.class))).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(any(UUID.class), any(UUID.class)))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.CREATE_PRODUCT))
                .thenReturn(true);

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.createNew(request);

        assertNotNull(result);
        assertEquals("SKU-123", result.getSku());
        assertEquals("Test Product", result.getName());
        assertEquals(user, result.getCreatedByUser());
        assertEquals(company, result.getCompany());
        assertEquals(request.getCurrentPrice(), result.getPreviousPrice());
        assertEquals(request.getCurrentPrice(), result.getNextPrice());
        assertEquals(request.getCurrentPrice(), result.getSalePrice());

        Mockito.verify(productRepository).save(any(Product.class));
    }

    @Test
    void getProductsByCompanyMember_throwsNullPointerException_whenUserIdIsNull() {
        UUID companyId = UUID.randomUUID();

        assertThrows(
                NullPointerException.class,
                () -> productService.getProductsByCompanyMember(null, companyId)
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void getProductsByCompanyMember_throwsNullPointerException_whenCompanyIdIsNull() {
        UUID userId = UUID.randomUUID();

        assertThrows(
                NullPointerException.class,
                () -> productService.getProductsByCompanyMember(userId, null)
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void getProductsByCompanyMember_returnsProducts_whenInputsAreValid() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Product product1 = new Product();
        Product product2 = new Product();

        List<Product> products = List.of(product1, product2);

        when(productRepository
                .findAllByCreatedByUserIdAndCompanyIdAndDateArchivedIsNullAndDateDeletedIsNull(
                        userId, companyId))
                .thenReturn(products);

        List<Product> result =
                productService.getProductsByCompanyMember(userId, companyId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(products, result);

        verify(productRepository)
                .findAllByCreatedByUserIdAndCompanyIdAndDateArchivedIsNullAndDateDeletedIsNull(
                        userId, companyId);
    }

    @Test
    void updateProduct_throwsNullPointerException_whenProductIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> productService.updateProduct(
                        null, UUID.randomUUID(), UUID.randomUUID(), updateRequest)
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void updateProduct_throwsNullPointerException_whenSkuIsNull() {
        updateRequest.setSku(null);

        assertThrows(
                NullPointerException.class,
                () -> productService.updateProduct(
                        UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), updateRequest)
        );
    }

    @Test
    void updateProduct_throwsUserNotFound_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> productService.updateProduct(
                        UUID.randomUUID(), UUID.randomUUID(), userId, updateRequest)
        );
    }

    @Test
    void updateProduct_throwsCompanyNotFound_whenCompanyDoesNotExist() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> productService.updateProduct(
                        UUID.randomUUID(), companyId, userId, updateRequest)
        );
    }

    @Test
    void updateProduct_throwsUserNotInCompany_whenUserIsNotMember() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotInCompanyException.class,
                () -> productService.updateProduct(
                        UUID.randomUUID(), companyId, userId, updateRequest)
        );
    }

    @Test
    void updateProduct_throwsInsufficientPermissions_whenUserLacksPermission() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.UPDATE_PRODUCT))
                .thenReturn(false);

        assertThrows(
                InsufficientPermissionsException.class,
                () -> productService.updateProduct(
                        UUID.randomUUID(), companyId, userId, updateRequest)
        );
    }

    @Test
    void updateProduct_throwsProductNotFound_whenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.UPDATE_PRODUCT))
                .thenReturn(true);
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.updateProduct(
                        productId, companyId, userId, updateRequest)
        );
    }

    @Test
    void updateProduct_throwsProductAlreadyExists_whenSkuChangedAndExists() {
        UUID productId = existingProduct.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.UPDATE_PRODUCT))
                .thenReturn(true);
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("NEW-SKU")).thenReturn(true);

        assertThrows(
                ProductAlreadyExistsException.class,
                () -> productService.updateProduct(
                        productId, companyId, userId, updateRequest)
        );
    }

    @Test
    void updateProduct_updatesFieldsAndPrices_whenAllChecksPass() {
        UUID productId = existingProduct.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.UPDATE_PRODUCT))
                .thenReturn(true);
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.existsBySku("NEW-SKU")).thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Product result = productService.updateProduct(
                productId, companyId, userId, updateRequest);

        assertEquals("NEW-SKU", result.getSku());
        assertEquals("Updated Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(20, result.getQuantityInStock());

        assertEquals(9.99, result.getPreviousPrice());
        assertEquals(19.99, result.getCurrentPrice());
        assertEquals(19.99, result.getNextPrice());
        assertEquals(17.99, result.getSalePrice());

        verify(productRepository).save(existingProduct);
    }

    @Test
    void archiveProduct_throwsNullPointerException_whenProductIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> productService.archiveProduct(
                        null, UUID.randomUUID(), UUID.randomUUID())
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void archiveProduct_throwsProductNotFound_whenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.archiveProduct(
                        productId, UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    void archiveProduct_throwsUserNotFound_whenUserDoesNotExist() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> productService.archiveProduct(
                        productId, UUID.randomUUID(), userId)
        );
    }

    @Test
    void archiveProduct_throwsCompanyNotFound_whenCompanyDoesNotExist() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> productService.archiveProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void archiveProduct_throwsUserNotInCompany_whenUserIsNotMember() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotInCompanyException.class,
                () -> productService.archiveProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void archiveProduct_throwsInsufficientPermissions_whenUserLacksPermission() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(false);

        assertThrows(
                InsufficientPermissionsException.class,
                () -> productService.archiveProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void archiveProduct_setsDateArchived_whenUserIsAuthorized() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(true);

        Instant before = Instant.now();

        productService.archiveProduct(productId, companyId, userId);

        assertNotNull(product.getDateArchived());
        assertTrue(product.getDateArchived().isAfter(before)
                || product.getDateArchived().equals(before));
    }

    @Test
    void archiveProduct_doesNothing_whenProductIsAlreadyArchived() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Instant archivedAt = Instant.now().minusSeconds(60);
        product.setDateArchived(archivedAt);

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(true);

        productService.archiveProduct(productId, companyId, userId);

        assertEquals(archivedAt, product.getDateArchived());
    }

    @Test
    void deleteProduct_throwsNullPointerException_whenProductIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> productService.deleteProduct(
                        null, UUID.randomUUID(), UUID.randomUUID())
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void deleteProduct_throwsProductNotFound_whenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.deleteProduct(
                        productId, UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    void deleteProduct_throwsUserNotFound_whenUserDoesNotExist() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> productService.deleteProduct(
                        productId, UUID.randomUUID(), userId)
        );
    }

    @Test
    void deleteProduct_throwsCompanyNotFound_whenCompanyDoesNotExist() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> productService.deleteProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void deleteProduct_throwsUserNotInCompany_whenUserIsNotMember() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotInCompanyException.class,
                () -> productService.deleteProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void deleteProduct_throwsInsufficientPermissions_whenUserLacksPermission() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.DELETE_PRODUCT))
                .thenReturn(false);

        assertThrows(
                InsufficientPermissionsException.class,
                () -> productService.deleteProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void deleteProduct_setsDateDeleted_whenUserIsAuthorized() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.DELETE_PRODUCT))
                .thenReturn(true);

        Instant before = Instant.now();

        productService.deleteProduct(productId, companyId, userId);

        assertNotNull(product.getDateDeleted());
        assertTrue(product.getDateDeleted().isAfter(before)
                || product.getDateDeleted().equals(before));
    }

    @Test
    void deleteProduct_doesNotModifyDateArchived() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Instant archivedAt = Instant.now().minusSeconds(120);
        product.setDateArchived(archivedAt);

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.DELETE_PRODUCT))
                .thenReturn(true);

        productService.deleteProduct(productId, companyId, userId);

        assertEquals(archivedAt, product.getDateArchived());
    }

    @Test
    void unarchiveProduct_throwsNullPointerException_whenProductIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> productService.unarchiveProduct(
                        null, UUID.randomUUID(), UUID.randomUUID())
        );

        verifyNoInteractions(productRepository);
    }

    @Test
    void unarchiveProduct_throwsProductNotFound_whenProductDoesNotExist() {
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.empty());

        assertThrows(
                ProductNotFoundException.class,
                () -> productService.unarchiveProduct(
                        productId, UUID.randomUUID(), UUID.randomUUID())
        );
    }

    @Test
    void unarchiveProduct_throwsUserNotFound_whenUserDoesNotExist() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> productService.unarchiveProduct(
                        productId, UUID.randomUUID(), userId)
        );
    }

    @Test
    void unarchiveProduct_throwsCompanyNotFound_whenCompanyDoesNotExist() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.empty());

        assertThrows(
                CompanyNotFoundException.class,
                () -> productService.unarchiveProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void unarchiveProduct_throwsUserNotInCompany_whenUserIsNotMember() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotInCompanyException.class,
                () -> productService.unarchiveProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void unarchiveProduct_throwsInsufficientPermissions_whenUserLacksPermission() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(false);

        assertThrows(
                InsufficientPermissionsException.class,
                () -> productService.unarchiveProduct(
                        productId, companyId, userId)
        );
    }

    @Test
    void unarchiveProduct_clearsDateArchived_whenProductIsArchived() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Instant archivedAt = Instant.now().minusSeconds(60);
        product.setDateArchived(archivedAt);

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(true);

        productService.unarchiveProduct(productId, companyId, userId);

        assertNull(product.getDateArchived());
    }

    @Test
    void unarchiveProduct_doesNothing_whenProductIsAlreadyUnarchived() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        product.setDateArchived(null);

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(true);

        productService.unarchiveProduct(productId, companyId, userId);

        assertNull(product.getDateArchived());
    }

    @Test
    void unarchiveProduct_doesNotModifyDateDeleted() {
        UUID productId = product.getId();
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        Instant deletedAt = Instant.now().minusSeconds(300);
        product.setDateDeleted(deletedAt);
        product.setDateArchived(Instant.now().minusSeconds(60));

        when(productRepository.findByIdAndDateDeletedIsNull(productId))
                .thenReturn(Optional.of(product));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
        when(companyMemberRepository.findByUserIdAndCompanyId(userId, companyId))
                .thenReturn(Optional.of(membership));
        when(permissionValidatorService.doesUserHavePerm(
                membership, PermissionKeys.ARCHIVE_PRODUCT))
                .thenReturn(true);

        productService.unarchiveProduct(productId, companyId, userId);

        assertEquals(deletedAt, product.getDateDeleted());
    }
}