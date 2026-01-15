package com.software.teamfive.jcc_product_inventory_management.controller;

import com.software.teamfive.jcc_product_inventory_management.model.inv.Product;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.product.CreateProductRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.product.UpdateProductPutRequest;
import com.software.teamfive.jcc_product_inventory_management.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/jcc/api/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public ResponseEntity<Product> createNewProduct(@Valid @RequestBody CreateProductRequest value) {
        final Product saved = this.productService.createNew(value);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{userId}/{companyId}")
    public ResponseEntity<List<Product>> getProductsByUserId(@PathVariable UUID userId, @PathVariable UUID companyId) {
        List<Product> products = this.productService.getProductsByCompanyMember(userId, companyId);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{companyId}/{userId}/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID productId, @PathVariable UUID companyId, @PathVariable UUID userId, @Valid @RequestBody UpdateProductPutRequest value) {
        Product updatedProduct = this.productService.updateProduct(productId, companyId, userId, value);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{companyId}/{userId}/{productId}/archive")
    public ResponseEntity<Void> archiveProduct(@PathVariable UUID productId, @PathVariable UUID userId, @PathVariable UUID companyId) {
        this.productService.archiveProduct(productId, companyId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{companyId}/{userId}/{productId}/delete")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId,  @PathVariable UUID userId, @PathVariable UUID companyId) {
        this.productService.deleteProduct(productId, companyId, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{companyId}/{userId}/{productId}/unarchive")
    public ResponseEntity<Void> unarchiveProduct(@PathVariable UUID productId,  @PathVariable UUID userId, @PathVariable UUID companyId) {
        this.productService.unarchiveProduct(productId, companyId, userId);
        return ResponseEntity.ok().build();
    }
}
