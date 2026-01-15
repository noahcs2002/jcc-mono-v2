package com.software.teamfive.jcc_product_inventory_management.repo;

import com.software.teamfive.jcc_product_inventory_management.model.audit.TransactionAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionAuditRepository extends JpaRepository<TransactionAudit, UUID> {
}
