package com.example.demo.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String address;

    @Column(length = 15)
    private String gstin;

    @Column(length = 50)
    private String state;

    @Column(name = "logo_url", length = 512)
    private String logoUrl;

    @Column(name = "invoice_number_format", length = 50)
    private String invoiceNumberFormat = "INV-{YYYY}-{SEQ}";

    @Column(name = "e_invoicing_enabled")
    private Boolean eInvoicingEnabled = false;

    @Column(length = 20)
    private String phone;

    @Column(length = 15)
    private String pan;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "bank_account", length = 50)
    private String bankAccount;

    @Column(name = "bank_ifsc", length = 20)
    private String bankIfsc;

    @Column(name = "bank_branch", length = 100)
    private String bankBranch;

    @Column(name = "template_name", length = 50)
    private String templateName = "default";

    @Column(name = "created_at", insertable = false, updatable = false)
    private ZonedDateTime createdAt;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    public String getInvoiceNumberFormat() { return invoiceNumberFormat; }
    public void setInvoiceNumberFormat(String invoiceNumberFormat) { this.invoiceNumberFormat = invoiceNumberFormat; }
    public Boolean geteInvoicingEnabled() { return eInvoicingEnabled; }
    public void seteInvoicingEnabled(Boolean eInvoicingEnabled) { this.eInvoicingEnabled = eInvoicingEnabled; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    public String getBankAccount() { return bankAccount; }
    public void setBankAccount(String bankAccount) { this.bankAccount = bankAccount; }
    public String getBankIfsc() { return bankIfsc; }
    public void setBankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; }
    public String getBankBranch() { return bankBranch; }
    public void setBankBranch(String bankBranch) { this.bankBranch = bankBranch; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
}
