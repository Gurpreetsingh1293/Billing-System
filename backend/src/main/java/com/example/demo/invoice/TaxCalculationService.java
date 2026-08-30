package com.example.demo.invoice;

import com.example.demo.domain.Client;
import com.example.demo.domain.Company;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TaxCalculationService {

    // Usually GST is 18% for parts, but we'll assume a standard rate for now or pass it in
    private static final BigDecimal DEFAULT_GST_RATE = new BigDecimal("0.18");

    public TaxSplit calculateTax(Company tenant, Client client, BigDecimal lineItemsTotal) {
        TaxSplit taxSplit = new TaxSplit();
        taxSplit.setTotalBeforeTax(lineItemsTotal);
        
        BigDecimal totalGst = lineItemsTotal.multiply(DEFAULT_GST_RATE).setScale(2, RoundingMode.HALF_UP);
        
        // If Tenant state and Client state are exactly the same, it's intra-state (CGST + SGST)
        // Otherwise, it's inter-state (IGST)
        if (tenant.getState() != null && client.getState() != null && 
            tenant.getState().trim().equalsIgnoreCase(client.getState().trim())) {
            
            BigDecimal halfGst = totalGst.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
            taxSplit.setCgst(halfGst);
            taxSplit.setSgst(totalGst.subtract(halfGst)); // avoid rounding discrepancy
            taxSplit.setIgst(BigDecimal.ZERO);
        } else {
            taxSplit.setCgst(BigDecimal.ZERO);
            taxSplit.setSgst(BigDecimal.ZERO);
            taxSplit.setIgst(totalGst);
        }
        
        taxSplit.setTotalWithTax(lineItemsTotal.add(totalGst));
        return taxSplit;
    }

    public static class TaxSplit {
        private BigDecimal totalBeforeTax;
        private BigDecimal cgst;
        private BigDecimal sgst;
        private BigDecimal igst;
        private BigDecimal totalWithTax;

        // Getters and Setters
        public BigDecimal getTotalBeforeTax() { return totalBeforeTax; }
        public void setTotalBeforeTax(BigDecimal totalBeforeTax) { this.totalBeforeTax = totalBeforeTax; }
        public BigDecimal getCgst() { return cgst; }
        public void setCgst(BigDecimal cgst) { this.cgst = cgst; }
        public BigDecimal getSgst() { return sgst; }
        public void setSgst(BigDecimal sgst) { this.sgst = sgst; }
        public BigDecimal getIgst() { return igst; }
        public void setIgst(BigDecimal igst) { this.igst = igst; }
        public BigDecimal getTotalWithTax() { return totalWithTax; }
        public void setTotalWithTax(BigDecimal totalWithTax) { this.totalWithTax = totalWithTax; }
    }
}
