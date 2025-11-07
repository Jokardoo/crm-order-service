package com.jokardo.crm.order_service.service.orderItem;

import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemRequest;
import com.jokardo.crm.order_service.domain.product.Product;
import com.jokardo.crm.order_service.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class OrderItemOperationService {
    private final ProductService productService;

    @Transactional
    public void updateOrderItemArticleAndPriceFromOrderItemRequest(OrderItem orderItem, OrderItemRequest orderItemRequest) {

        if ( orderItemRequest.getProductArticle() != null && !orderItemRequest.getProductArticle().isEmpty()) {

            if (orderItemRequest.getImages() != null) {
                throw new IllegalArgumentException("You can not add image to item with 'product article' != null!");
            }

            Product productFromDatabase = productService.getByProductArticle(orderItemRequest.getProductArticle());

            orderItem.setProductName(productFromDatabase.getProductName());
            orderItem.setProductArticle(productFromDatabase.getProductArticle());
            orderItem.setPrice(productFromDatabase.getPrice());
            orderItem.setDescription("none");
            orderItem.setPrice(productFromDatabase.getPrice());
        }
        else {
            orderItem.setProductArticle("none");
            orderItem.setProductName(orderItemRequest.getName());
            orderItem.setDescription(orderItemRequest.getDescription());
            orderItem.setPrice(BigDecimal.ZERO);
        }
    }



}
