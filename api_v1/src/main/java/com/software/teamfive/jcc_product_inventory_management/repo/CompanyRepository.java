package com.software.teamfive.jcc_product_inventory_management.repo;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
