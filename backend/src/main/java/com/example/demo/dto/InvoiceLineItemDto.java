package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class InvoiceLineItemDto {
    private UUID productId;
    private BigDecimal quantity;
    private BigDecimal rate;

    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
}
