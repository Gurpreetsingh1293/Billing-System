package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductDto {
    private UUID id;
    private String name;
    private String hsnCode;
    private String unit;
    private BigDecimal defaultSaleRate;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getHsnCode() { return hsnCode; }
    public void setHsnCode(String hsnCode) { this.hsnCode = hsnCode; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getDefaultSaleRate() { return defaultSaleRate; }
    public void setDefaultSaleRate(BigDecimal defaultSaleRate) { this.defaultSaleRate = defaultSaleRate; }
}
