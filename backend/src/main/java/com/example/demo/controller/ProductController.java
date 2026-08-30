package com.example.demo.controller;

import com.example.demo.config.TenantContext;
import com.example.demo.domain.Product;
import com.example.demo.dto.ProductDto;
import com.example.demo.product.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.getAllProducts().stream().map(this::toDto).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@RequestBody ProductDto dto) {
        Product product = new Product();
        product.setTenantId(TenantContext.getCurrentTenant());
        product.setName(dto.getName());
        product.setHsnCode(dto.getHsnCode());
        product.setUnit(dto.getUnit());
        product.setDefaultSaleRate(dto.getDefaultSaleRate());
        
        Product saved = productService.createProduct(product);
        return toDto(saved);
    }
    
    private ProductDto toDto(Product p) {
        ProductDto dto = new ProductDto();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setHsnCode(p.getHsnCode());
        dto.setUnit(p.getUnit());
        dto.setDefaultSaleRate(p.getDefaultSaleRate());
        return dto;
    }
}
