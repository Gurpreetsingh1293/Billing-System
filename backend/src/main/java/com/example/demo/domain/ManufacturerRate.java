package com.example.demo.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "manufacturer_rate")
public class ManufacturerRate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "manufacturer_name", nullable = false)
    private String manufacturerName;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal rate;

    @Column(name = "date_recorded", insertable = false, updatable = false)
    private ZonedDateTime dateRecorded;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTenantId() { return tenantId; }
    public void setTenantId(UUID tenantId) { this.tenantId = tenantId; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getManufacturerName() { return manufacturerName; }
    public void setManufacturerName(String manufacturerName) { this.manufacturerName = manufacturerName; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public ZonedDateTime getDateRecorded() { return dateRecorded; }
    public void setDateRecorded(ZonedDateTime dateRecorded) { this.dateRecorded = dateRecorded; }
}
