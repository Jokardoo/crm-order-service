package com.jokardo.crm.order_service.repository;

import com.jokardo.crm.order_service.domain.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByProductArticle(String productArticle);

    Optional<ProductEntity> findByProductArticle(String productArticle);

    void deleteByProductArticle(String productArticle);

    List<ProductEntity> findAllByProductNameContaining(String productName);

}
