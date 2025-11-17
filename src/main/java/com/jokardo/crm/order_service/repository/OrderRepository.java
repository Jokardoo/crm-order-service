package com.jokardo.crm.order_service.repository;

import com.jokardo.crm.order_service.domain.order.OrderEntity;
import com.jokardo.crm.order_service.domain.order.OrderStatusEnum;
import com.jokardo.crm.order_service.exceptions.order.OrderNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomerId(Long customerId);

    List<OrderEntity> findByStatus(OrderStatusEnum status);

    @Query(value = """
    SELECT image FROM orders_items_images oii 
        LEFT JOIN order_item oi 
                ON oii.order_item_entity_id = oi.id
        WHERE oi.order_id = :orderId

    """, nativeQuery = true)
    List<String> findImagesNamesByOrderId(Long orderId);

    @Modifying
    @Query("""
        UPDATE OrderEntity o
        SET o.status = :status
        WHERE o.id = :orderId
    """)
    void updateOrderStatus(@Param("orderId") Long orderId, @Param("status") OrderStatusEnum status);

    @Query(value = """
    SELECT oe.id, oe.city, oe.street, oe.status, oe.customer_id, oe.created_at, oe.postal_code 
    FROM orders oe
    LEFT JOIN customer c ON oe.customer_id = c.id 
    WHERE c.phone_number = :phoneNumber
    """, nativeQuery = true)
    List<OrderEntity> findByCustomerPhoneNumber(@Param("phoneNumber") String phoneNumber);
}