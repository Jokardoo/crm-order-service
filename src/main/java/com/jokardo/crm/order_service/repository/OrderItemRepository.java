package com.jokardo.crm.order_service.repository;

import com.jokardo.crm.order_service.domain.order.order_item.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    @Query(value = """
    SELECT FROM order_item oi 
        WHERE order_id=:orderId 
        AND index_in_order=:indexInOrder
""", nativeQuery = true)
    Optional<OrderItemEntity> findByOrderIdAndItemIndex(@Param("orderId") Long id, @Param("indexInOrder") Integer indexInOrder);
}
