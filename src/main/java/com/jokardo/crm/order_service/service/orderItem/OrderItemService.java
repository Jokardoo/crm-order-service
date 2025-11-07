package com.jokardo.crm.order_service.service.orderItem;

import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderEntity;
import com.jokardo.crm.order_service.domain.order.order_item.*;
import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import com.jokardo.crm.order_service.exceptions.order.OrderCannotBeUpdatedException;
import com.jokardo.crm.order_service.exceptions.order.OrderItemNotFoundException;
import com.jokardo.crm.order_service.exceptions.order.OrderNotFoundException;
import com.jokardo.crm.order_service.mapper.image.OrderItemImageModelToDtoMapper;
import com.jokardo.crm.order_service.mapper.orderItem.OrderItemModelToEntityMapper;
import com.jokardo.crm.order_service.repository.OrderItemRepository;
import com.jokardo.crm.order_service.service.ImageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderItemUtil orderItemUtil;

    private final OrderItemModelToEntityMapper orderItemModelToEntityMapper;
    private final OrderItemImageModelToDtoMapper orderItemImageModelToDtoMapper;

    private final ImageService imageService;
    private final OrderItemOperationService orderItemOperationService;

    public OrderItem save(OrderItem orderItem) {
        OrderItemEntity savedOrderItem = orderItemRepository.save(
                orderItemModelToEntityMapper.toEntity(orderItem));

        return orderItemModelToEntityMapper.toModel(savedOrderItem);
    }

    public List<OrderItem> saveAll(List<OrderItem> orderItems) {
        log.info("Called method saveAll(List<OrderItem> orderItem): {}", orderItems);

        List<OrderItemEntity> entitiesToSave = orderItemModelToEntityMapper.toEntity(orderItems);

        return orderItemModelToEntityMapper.toModel(orderItemRepository.saveAll(entitiesToSave));
    }

    @Transactional
    public void uploadOrderItemImage(Long orderItemId, OrderItemImage orderItemImage) {
        OrderItemEntity foundOrderItemEntity = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new OrderItemNotFoundException("Order item with id " + orderItemId + " not found!"));

        OrderItem orderItem = orderItemModelToEntityMapper
                .toModel(foundOrderItemEntity);

        String fileName = imageService.uploadOrderItemImage(orderItemImage);
        orderItem.getImages().add(fileName);

        orderItemRepository.save(
                orderItemModelToEntityMapper.toEntity(orderItem)
        );
    }
    @Transactional()
    public List<OrderItem> parseOrderItemsRequests(List<OrderItemRequest> orderItemRequestList) {
        List<OrderItem> orderItems = new ArrayList<>();

        int currentOrderItemIndex = 1;

        //TODO заменить на стрим
        for (OrderItemRequest orderItemRequest : orderItemRequestList) {
            OrderItem orderItem = new OrderItem();

            orderItemOperationService.updateOrderItemArticleAndPriceFromOrderItemRequest(orderItem, orderItemRequest);

            List<OrderItemImage> orderItemImages = orderItemImageModelToDtoMapper.toModel(orderItemRequest.getImages());

            orderItemImages.forEach(oii ->
                    orderItem
                            .getImages()
                            .add(imageService.uploadOrderItemImage(oii))
            );
            orderItem.setQuantity(orderItemRequest.getQuantity());
            orderItem.setIndexInOrder(currentOrderItemIndex);
            orderItems.add(orderItem);

            currentOrderItemIndex++;
        }
        return orderItems;
    }

    @Transactional
    public OrderItem updateOrderItem(@Valid @RequestParam OrderItemUpdateRequest updateRequest) {
        log.info("Called update method: {}", updateRequest);

        OrderItemEntity foundOrderItemEntity = orderItemRepository
                .findByOrderIdAndItemIndex(updateRequest.getOrderId(), updateRequest.getOrderItemIndex())
                .orElseThrow(() -> new OrderItemNotFoundException("Order with id ='" + updateRequest.getOrderId()
                        + "' and item index = '" + updateRequest.getOrderItemIndex() + "' not found!"));

        OrderItem orderItemForUpdate = orderItemModelToEntityMapper.toModel(foundOrderItemEntity);

        orderItemUtil.updateAllOrderItemFields(updateRequest, orderItemForUpdate);

        OrderItemEntity updatedOrderItemEntity = orderItemModelToEntityMapper.toEntity(orderItemForUpdate);

        return orderItemModelToEntityMapper.toModel(orderItemRepository.save(updatedOrderItemEntity));
    }


}
