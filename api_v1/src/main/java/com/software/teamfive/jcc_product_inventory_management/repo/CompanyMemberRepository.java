package com.software.teamfive.jcc_product_inventory_management.repo;

import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyMemberRepository extends JpaRepository<CompanyMember, UUID> {

    Optional<CompanyMember> findByUserId(UUID id);

    Optional<CompanyMember> findByUserIdAndCompanyId(UUID id, UUID id1);
}
