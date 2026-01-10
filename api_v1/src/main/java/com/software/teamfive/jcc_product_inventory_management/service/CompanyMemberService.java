package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.repo.CompanyMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyMemberService {

    private final CompanyMemberRepository companyMemberRepository;

    @Autowired
    public CompanyMemberService(CompanyMemberRepository companyMemberRepository) {
        this.companyMemberRepository = companyMemberRepository;
    }

    public boolean isMemberOfCompany(User member, Company company) {
        CompanyMember companyMember = this.companyMemberRepository
                .findByUserIdAndCompanyId(member.getId(), company.getId())
                .orElse(null);

        return companyMember != null;
    }

    public Optional<CompanyMember> getCompanyMemberForUserAndCompany(User user, Company company) {
        return this.companyMemberRepository.findByUserIdAndCompanyId(user.getId(), company.getId());
    }
}
