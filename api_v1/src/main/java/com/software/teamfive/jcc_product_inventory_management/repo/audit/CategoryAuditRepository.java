package com.software.teamfive.jcc_product_inventory_management.repo.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryAuditRepository extends JpaRepository<CategoryAudit, UUID> {
}
