package com.example.demo.dto;

import java.util.List;
import java.util.UUID;

public class InvoiceRequestDto {
    private UUID clientId;
    private List<InvoiceLineItemDto> items;

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }
    public List<InvoiceLineItemDto> getItems() { return items; }
    public void setItems(List<InvoiceLineItemDto> items) { this.items = items; }
}
