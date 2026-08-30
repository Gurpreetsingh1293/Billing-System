package com.example.demo.controller;

import com.example.demo.config.TenantContext;
import com.example.demo.domain.Client;
import com.example.demo.domain.Company;
import com.example.demo.domain.Invoice;
import com.example.demo.domain.InvoiceLineItem;
import com.example.demo.domain.Product;
import com.example.demo.dto.InvoiceRequestDto;
import com.example.demo.dto.InvoiceResponseDto;
import com.example.demo.invoice.InvoiceService;
import com.example.demo.pdf.PdfGenerationService;
import com.example.demo.repository.ClientRepository;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.InvoiceRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final PdfGenerationService pdfGenerationService;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final InvoiceRepository invoiceRepository;
    private final CompanyRepository companyRepository;

    public InvoiceController(InvoiceService invoiceService, PdfGenerationService pdfGenerationService, 
                             ClientRepository clientRepository, ProductRepository productRepository,
                             InvoiceRepository invoiceRepository, CompanyRepository companyRepository) {
        this.invoiceService = invoiceService;
        this.pdfGenerationService = pdfGenerationService;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.invoiceRepository = invoiceRepository;
        this.companyRepository = companyRepository;
    }

    @GetMapping
    public List<InvoiceResponseDto> listInvoices() {
        return invoiceRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponseDto createDraft(@RequestBody InvoiceRequestDto request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
                
        List<InvoiceLineItem> items = request.getItems().stream().map(dto -> {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));
            InvoiceLineItem item = new InvoiceLineItem();
            item.setProduct(product);
            item.setQuantity(dto.getQuantity());
            item.setRate(dto.getRate());
            return item;
        }).collect(Collectors.toList());

        Invoice draft = invoiceService.createDraftInvoice(TenantContext.getCurrentTenant(), client, items);
        return toDto(draft);
    }

    @PostMapping("/{id}/issue")
    public InvoiceResponseDto issueInvoice(@PathVariable UUID id) {
        Invoice issued = invoiceService.issueInvoice(id);
        return toDto(issued);
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
                
        Company company = companyRepository.findById(invoice.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
                
        byte[] pdfBytes = pdfGenerationService.generateInvoicePdf(invoice, company);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "draft";
        headers.setContentDispositionFormData("filename", "invoice-" + filename + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    private InvoiceResponseDto toDto(Invoice i) {
        InvoiceResponseDto dto = new InvoiceResponseDto();
        dto.setId(i.getId());
        dto.setInvoiceNumber(i.getInvoiceNumber());
        dto.setStatus(i.getStatus());
        dto.setClientName(i.getClient() != null ? i.getClient().getName() : null);
        dto.setTotalAmount(i.getTotalAmount());
        dto.setCgstAmount(i.getCgstAmount());
        dto.setSgstAmount(i.getSgstAmount());
        dto.setIgstAmount(i.getIgstAmount());
        dto.setCreatedAt(i.getCreatedAt());
        dto.setIssuedAt(i.getIssuedAt());
        dto.setIrn(i.getIrn());
        return dto;
    }
}
