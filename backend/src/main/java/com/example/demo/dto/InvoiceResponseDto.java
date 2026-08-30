package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public class InvoiceResponseDto {
    private UUID id;
    private String invoiceNumber;
    private String status;
    private String clientName;
    private BigDecimal totalAmount;
    private BigDecimal cgstAmount;
    private BigDecimal sgstAmount;
    private BigDecimal igstAmount;
    private ZonedDateTime createdAt;
    private ZonedDateTime issuedAt;
    private String irn;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }
    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }
    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }
    public ZonedDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(ZonedDateTime createdAt) { this.createdAt = createdAt; }
    public ZonedDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(ZonedDateTime issuedAt) { this.issuedAt = issuedAt; }
    public String getIrn() { return irn; }
    public void setIrn(String irn) { this.irn = irn; }
}
