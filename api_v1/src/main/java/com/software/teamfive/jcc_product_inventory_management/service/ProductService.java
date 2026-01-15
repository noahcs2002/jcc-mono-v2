package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.inv.Product;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.product.CreateProductRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.product.UpdateProductPutRequest;
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
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CompanyMemberRepository companyMemberRepository;
    private final CompanyRepository companyRepository;
    private final PermissionValidatorService permissionValidatorService;

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository, CompanyMemberRepository companyMemberRepository, CompanyRepository companyRepository, PermissionValidatorService permissionValidatorService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.companyMemberRepository = companyMemberRepository;
        this.companyRepository = companyRepository;
        this.permissionValidatorService = permissionValidatorService;
    }

    public Product createNew(CreateProductRequest request) {

        if (productRepository.existsBySku(request.getSku())) {
            throw new ProductAlreadyExistsException(request.getSku());
        }

        User user = this.userRepository.findById(request.getCreatedByUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getCreatedByUserId()));

        Company company = this.companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException(request.getCompanyId()));

        CompanyMember membership = this.companyMemberRepository.findByUserIdAndCompanyId(user.getId(), company.getId())
                .orElseThrow(() -> new RuntimeException("User is not a member of the specified company"));

        if(!permissionValidatorService.doesUserHavePerm(membership, PermissionKeys.CREATE_PRODUCT)) {
            throw new InsufficientPermissionsException(user.getId(), PermissionKeys.CREATE_PRODUCT);
        }

        Product product = new Product();
        product.setSku(request.getSku());
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setQuantityInStock(request.getQuantityInStock());
        product.setCurrentPrice(request.getCurrentPrice());
        product.setPreviousPrice(request.getCurrentPrice());
        product.setNextPrice(request.getCurrentPrice());
        product.setSalePrice(request.getCurrentPrice());
        product.setCreatedByUser(user);
        product.setCompany(company);

        return productRepository.save(product);
    }

    public List<Product> getProductsByCompanyMember(UUID userId, UUID companyId) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(companyId);
        return productRepository
                .findAllByCreatedByUserIdAndCompanyIdAndDateArchivedIsNullAndDateDeletedIsNull(userId, companyId);
    }

    @Transactional
    public Product updateProduct(UUID productId, UUID companyId, UUID userId, @Valid UpdateProductPutRequest request) {
        Objects.requireNonNull(productId);
        Objects.requireNonNull(request.getSku());
        Objects.requireNonNull(request.getName());
        Objects.requireNonNull(request.getDescription());
        Objects.requireNonNull(request.getCurrentPrice());
        Objects.requireNonNull(request);

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Company company = this.companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        CompanyMember companyMember = this.companyMemberRepository.findByUserIdAndCompanyId(userId, companyId)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        if(!permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.UPDATE_PRODUCT)) {
            throw new InsufficientPermissionsException(userId, PermissionKeys.UPDATE_PRODUCT);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        if (!product.getSku().equals(request.getSku())) {

            if (productRepository.existsBySku(request.getSku())) {
                throw new ProductAlreadyExistsException(request.getSku());
            }

            product.setSku(request.getSku());
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setQuantityInStock(request.getQuantityInStock());

        if (!product.getCurrentPrice().equals(request.getCurrentPrice())) {
            product.setPreviousPrice(product.getCurrentPrice());
            product.setCurrentPrice(request.getCurrentPrice());
            product.setNextPrice(request.getCurrentPrice());
        }
        product.setSalePrice(request.getSalePrice());

        return this.productRepository.save(product);
    }

    @Transactional
    public void archiveProduct(UUID productId, UUID companyId, UUID userId) {
        Objects.requireNonNull(productId);
        Objects.requireNonNull(companyId);
        Objects.requireNonNull(userId);

        Product product = productRepository.findByIdAndDateDeletedIsNull(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Company company = this.companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        CompanyMember companyMember = this.companyMemberRepository.findByUserIdAndCompanyId(userId, companyId)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        if(!this.permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.ARCHIVE_PRODUCT)) {
            throw new  InsufficientPermissionsException(userId, PermissionKeys.ARCHIVE_PRODUCT);
        }

        if (product.getDateArchived() == null) {
            product.setDateArchived(Instant.now());
        }
    }

    @Transactional
    public void deleteProduct(UUID productId,  UUID companyId, UUID userId) {
        Objects.requireNonNull(productId);
        Objects.requireNonNull(companyId);
        Objects.requireNonNull(userId);

        Product product = productRepository.findByIdAndDateDeletedIsNull(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Company company = this.companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        CompanyMember companyMember = this.companyMemberRepository.findByUserIdAndCompanyId(userId, companyId)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        if(!this.permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.DELETE_PRODUCT)) {
            throw new  InsufficientPermissionsException(userId, PermissionKeys.DELETE_PRODUCT);
        }

        product.setDateDeleted(Instant.now());
    }

    @Transactional
    public void unarchiveProduct(UUID productId,  UUID companyId, UUID userId) {
        Objects.requireNonNull(productId);
        Objects.requireNonNull(companyId);
        Objects.requireNonNull(userId);

        Product product = productRepository.findByIdAndDateDeletedIsNull(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Company company = this.companyRepository.findById(companyId)
                .orElseThrow(() -> new CompanyNotFoundException(companyId));

        CompanyMember companyMember = this.companyMemberRepository.findByUserIdAndCompanyId(userId, companyId)
                .orElseThrow(() -> new UserNotInCompanyException(userId, companyId));

        if(!this.permissionValidatorService.doesUserHavePerm(companyMember, PermissionKeys.ARCHIVE_PRODUCT)) {
            throw new  InsufficientPermissionsException(userId, PermissionKeys.ARCHIVE_PRODUCT);
        }

        if (product.getDateArchived() != null) {
            product.setDateArchived(null);
        }
    }
}
