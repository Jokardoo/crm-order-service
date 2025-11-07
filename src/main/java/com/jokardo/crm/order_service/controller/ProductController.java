package com.jokardo.crm.order_service.controller;

import com.jokardo.crm.order_service.domain.product.Product;
import com.jokardo.crm.order_service.domain.product.ProductDto;
import com.jokardo.crm.order_service.mapper.product.ProductModelToDtoMapper;
import com.jokardo.crm.order_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final ProductModelToDtoMapper productModelToDtoMapper;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping("/create")
    public ResponseEntity<ProductDto> create(@RequestBody @Valid ProductDto productDto) {

        Product createdProduct = productService
                .createProduct(productModelToDtoMapper.toModel(productDto));
        return ResponseEntity.ok(productModelToDtoMapper.toDto(createdProduct));
    }



    @PutMapping("/update")
    public ResponseEntity<ProductDto> update(@RequestBody @Valid ProductDto productDto) {
        Product updatedpProduct = productService.updateProduct(productModelToDtoMapper.toModel(productDto));

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productModelToDtoMapper.toDto(updatedpProduct));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteByProductArticle(@RequestBody String productArticle) {

        productService.deleteByProductArticle(productArticle);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/{article}")
    public ResponseEntity<ProductDto> getByProductArticle(@PathVariable(required = true) String article) {
        Product product = productService.getByProductArticle(article);
        return ResponseEntity.ok(productModelToDtoMapper.toDto(product));
    }

    @GetMapping("/find")
    public ResponseEntity<List<ProductDto>> getAllByProductName(@RequestParam(required = true) String productName) {
        List<Product> foundProducts = productService.getAllByProductNameContaining(productName);
        return ResponseEntity.ok().body(productModelToDtoMapper.toDto(foundProducts));
    }
}
