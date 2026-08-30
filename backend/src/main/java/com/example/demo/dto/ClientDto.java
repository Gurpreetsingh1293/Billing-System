package com.example.demo.dto;

import java.util.UUID;

public class ClientDto {
    private UUID id;
    private String name;
    private String billingAddress;
    private String shippingAddress;
    private String gstin;
    private String state;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBillingAddress() { return billingAddress; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getGstin() { return gstin; }
    public void setGstin(String gstin) { this.gstin = gstin; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
