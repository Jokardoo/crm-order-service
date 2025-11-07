package com.jokardo.crm.order_service.service;

import com.jokardo.crm.order_service.domain.product.Product;
import com.jokardo.crm.order_service.domain.product.ProductEntity;
import com.jokardo.crm.order_service.exceptions.product.ProductAlreadyExistsException;
import com.jokardo.crm.order_service.exceptions.product.ProductNotFoundException;
import com.jokardo.crm.order_service.exceptions.security.NotEnoughRightsException;
import com.jokardo.crm.order_service.mapper.product.ProductModelToEntityMapper;
import com.jokardo.crm.order_service.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ProductModelToEntityMapper productModelToEntityMapper;

    @Transactional(readOnly = true)
    public boolean existsByProductArticle(String productArticle) {
        return productRepository.existsByProductArticle(productArticle);
    }

    @Transactional
    public Product createProduct(Product product) {
        logger.info("Creating product {}", product);

        if (productRepository.existsByProductArticle(product.getProductArticle()))
            throw new ProductAlreadyExistsException("Product with article " + product.getProductArticle() + " already exists");

        ProductEntity entityToCreate = productModelToEntityMapper.toEntity(product);
        ProductEntity createdProductEntity = productRepository.save(entityToCreate);

        logger.info("Product created {}", createdProductEntity);
        return productModelToEntityMapper.toModel(createdProductEntity);
    }

    @Transactional
    public Product updateProduct(Product product) {

        ProductEntity foundProductEntity = productRepository.findByProductArticle(product.getProductArticle())
                .orElseThrow(
                        () -> {
                            String exceptionMessage = "Product wiith article " + product.getProductArticle() + " not found";
                            logger.warn(exceptionMessage);
                            return new ProductNotFoundException(exceptionMessage);
                        }
                );

        Product productToUpdate = productModelToEntityMapper.toModel(foundProductEntity);
        updateOnlyChangedFields(product, productToUpdate);

        ProductEntity updatedProductEntity = productRepository.save(productModelToEntityMapper.toEntity(productToUpdate));
        logger.info("Product updated {}", updatedProductEntity);
        return productModelToEntityMapper.toModel(updatedProductEntity);

    }

    @Transactional(readOnly = true)
    public Product getByProductArticle(String productArticle) {
        return productModelToEntityMapper.toModel(productRepository.findByProductArticle(productArticle)
                .orElseThrow(() -> {
            String exceptionMessage = "Product article " + productArticle + " not found";
            logger.warn(exceptionMessage);
            return new ProductNotFoundException(exceptionMessage);
            })
        );
    }

    @Transactional(readOnly = true)
    public List<Product> getAllByProductNameContaining(String productName) {
        logger.info("Getting all products by name containing: {}", productName);

        List<ProductEntity> foundProductEntities = productRepository.findAllByProductNameContaining(productName);
        List<Product> foundProducts = productModelToEntityMapper.toModel(foundProductEntities);

        return foundProducts;
    }

    @Transactional
    public void deleteByProductArticle(String productArticle) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            String exceptionMessage = "To remove a product, you must have admin rights";
            logger.warn(exceptionMessage);
            throw new NotEnoughRightsException(exceptionMessage);
        }

        if (!productRepository.existsByProductArticle(productArticle)) {
            String exceptionMessage = "Product with article " + productArticle + " not found";
            logger.warn(exceptionMessage);
            throw new ProductNotFoundException(exceptionMessage);
        }

        productRepository.deleteByProductArticle(productArticle);
        logger.info("Product with article {} was deleted ", productArticle);
    }

    // Метод проходится по всем полям класса и смотрит, есть ли различия в полях target и source.
    // Если такие есть - он заменяет значения
    private void updateOnlyChangedFields(Product source, Product target) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("Source or target is null. Cannot update the product.");
        }

        Arrays.stream(target.getClass().getDeclaredFields())
                .forEach(field -> {
                    try {
                        field.setAccessible(true);

                        // Берем конкретное поле у source
                        Object sourceValue = field.get(source);
                        if (sourceValue != null) {

                            // Берем конкретное поле у target
                            Object targetValue = field.get(target);
                            if (targetValue != null) {
                                field.set(target, sourceValue);
                            }

                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
