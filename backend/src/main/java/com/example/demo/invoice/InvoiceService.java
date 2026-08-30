package com.example.demo.invoice;

import com.example.demo.domain.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;
    private final TaxCalculationService taxCalculationService;
    private final InvoiceNumberGenerator invoiceNumberGenerator;

    public InvoiceService(InvoiceRepository invoiceRepository, CompanyRepository companyRepository,
                          TaxCalculationService taxCalculationService, InvoiceNumberGenerator invoiceNumberGenerator) {
        this.invoiceRepository = invoiceRepository;
        this.companyRepository = companyRepository;
        this.taxCalculationService = taxCalculationService;
        this.invoiceNumberGenerator = invoiceNumberGenerator;
    }

    @Transactional
    public Invoice createDraftInvoice(UUID companyId, Client client, List<InvoiceLineItem> items) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        Invoice invoice = new Invoice();
        invoice.setTenantId(companyId);
        invoice.setClient(client);
        invoice.setStatus("DRAFT");
        
        BigDecimal lineItemsTotal = BigDecimal.ZERO;
        
        for (InvoiceLineItem item : items) {
            item.setTenantId(companyId);
            item.setInvoice(invoice);
            
            BigDecimal lineTotal = item.getQuantity().multiply(item.getRate());
            item.setLineTotal(lineTotal);
            lineItemsTotal = lineItemsTotal.add(lineTotal);
        }
        
        invoice.setLineItems(items);

        TaxCalculationService.TaxSplit taxSplit = taxCalculationService.calculateTax(company, client, lineItemsTotal);
        invoice.setTotalAmount(taxSplit.getTotalWithTax());
        invoice.setCgstAmount(taxSplit.getCgst());
        invoice.setSgstAmount(taxSplit.getSgst());
        invoice.setIgstAmount(taxSplit.getIgst());
        
        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice issueInvoice(UUID invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        if (!"DRAFT".equals(invoice.getStatus())) {
            throw new IllegalStateException("Only DRAFT invoices can be issued");
        }

        Company company = companyRepository.findById(invoice.getTenantId())
                .orElseThrow(() -> new IllegalStateException("Company not found for invoice"));

        String invoiceNumber = invoiceNumberGenerator.generateNextInvoiceNumber(company);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setStatus(company.geteInvoicingEnabled() ? "PENDING_IRN" : "ISSUED");
        invoice.setIssuedAt(ZonedDateTime.now());

        return invoiceRepository.save(invoice);
    }
}
