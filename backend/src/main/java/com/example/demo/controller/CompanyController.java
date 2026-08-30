package com.example.demo.controller;

import com.example.demo.config.TenantContext;
import com.example.demo.domain.Company;
import com.example.demo.repository.CompanyRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/company")
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentCompany() {
        Company company = companyRepository.findById(TenantContext.getCurrentTenant())
                .orElseThrow(() -> new IllegalStateException("Company not found"));

        return Map.of(
                "id", company.getId(),
                "name", company.getName(),
                "address", company.getAddress() != null ? company.getAddress() : "",
                "gstin", company.getGstin() != null ? company.getGstin() : "",
                "state", company.getState() != null ? company.getState() : "",
                "phone", company.getPhone() != null ? company.getPhone() : ""
        );
    }
}
