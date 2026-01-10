package com.software.teamfive.jcc_product_inventory_management.repo;

import com.software.teamfive.jcc_product_inventory_management.model.inv.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySku(String sku);
    Optional<Product> findByIdAndDateDeletedIsNull(UUID productId);
    List<Product> findAllByCreatedByUserIdAndCompanyIdAndDateArchivedIsNullAndDateDeletedIsNull(UUID userId, UUID companyId);
}

