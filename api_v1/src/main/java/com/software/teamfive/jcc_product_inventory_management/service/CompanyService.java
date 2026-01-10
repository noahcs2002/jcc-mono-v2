package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.repo.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {


    private final CompanyRepository companyRepository;

    @Autowired
    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }
}
