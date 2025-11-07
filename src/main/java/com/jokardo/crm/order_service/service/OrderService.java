package com.jokardo.crm.order_service.service;

import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.customer.CustomerEntity;
import com.jokardo.crm.order_service.domain.order.*;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItem;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemEntity;
import com.jokardo.crm.order_service.exceptions.order.OrderCannotBeUpdatedException;
import com.jokardo.crm.order_service.exceptions.order.OrderNotFoundException;
import com.jokardo.crm.order_service.kafka.order.OrderSender;
import com.jokardo.crm.order_service.mapper.address.AddressModelToDtoMapper;
import com.jokardo.crm.order_service.mapper.customer.CustomerModelToEntityMapper;
import com.jokardo.crm.order_service.mapper.order.OrderModelToEntityMapper;
import com.jokardo.crm.order_service.mapper.orderItem.OrderItemModelToDtoMapper;
import com.jokardo.crm.order_service.mapper.orderItem.OrderItemModelToEntityMapper;
import com.jokardo.crm.order_service.repository.CustomerRepository;
import com.jokardo.crm.order_service.repository.OrderItemRepository;
import com.jokardo.crm.order_service.repository.OrderRepository;
import com.jokardo.crm.order_service.service.orderItem.OrderItemService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final OrderUtil orderUtil;

    private final CustomerService customerService;
    private final OrderItemService orderItemService;
    private final ImageService imageService;

    private final AddressModelToDtoMapper addressModelToDtoMapper;
    private final OrderModelToEntityMapper orderModelToEntityMapper;
    private final OrderItemModelToEntityMapper orderItemModelToEntityMapper;

    private final CustomerModelToEntityMapper customerModelToEntityMapper;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;

    private final OrderSender orderSender;
    private final OrderItemModelToDtoMapper orderItemModelToDtoMapper;

    @Transactional
    public Order createOrder(OrderRequest request) {
        logger.info("Called createOrder method: {}", request);

        Order order = new Order();

        if (request.getItems() == null || request.getItems().isEmpty())
            throw new IllegalArgumentException("Order items cannot be empty");

        addCustomerFromOrderRequestToOrder(request, order); //TODO создать для этого отдельный сервис

        order.setStatus(OrderStatusEnum.NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setDeliveryAddress(addressModelToDtoMapper.toModel(request.getDeliveryAddress()));

        List<OrderItem> parsedOrderItems = orderItemService.parseOrderItemsRequests(request.getItems());
        List<OrderItemEntity> orderItemEntities = orderItemModelToEntityMapper.toEntity(parsedOrderItems);

        OrderEntity orderEntity = orderModelToEntityMapper.toEntity(order);
        setRelationsBetweenOrderItemEntityAndOrderEntity(orderEntity, orderItemEntities);

        OrderEntity savedOrderEntity = orderRepository.save(orderEntity);

        return orderModelToEntityMapper.toModel(savedOrderEntity);
    }



    @Transactional
    public void deleteByOrderId(Long id) {
        logger.info("Called deleteByOrderId: {}", id);
        List<String> imageNames = orderRepository.findImagesNamesByOrderId(id);

        imageNames.forEach(imageService::deleteImageByImageName);


        orderRepository.deleteById(id);
    }


    @Transactional
    public Order updateOrderInfo(Long id, OrderUpdateRequest orderUpdateRequest) {
        logger.info("Called updateOrderInfo method: {}", orderUpdateRequest);

        Order foundOrder = orderModelToEntityMapper.toModel(orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id not found!")));

        if (!isOrderCanBeUpdated(foundOrder))
            throw new OrderCannotBeUpdatedException("To update order, its status should be not " + foundOrder.getStatus());


        orderUtil.updateAllOrderFields(foundOrder, orderUpdateRequest);
        OrderEntity updatedOrder = orderRepository.save(orderModelToEntityMapper.toEntity(foundOrder));

        return orderModelToEntityMapper.toModel(updatedOrder);
    }

    @Transactional
    public void approveOrder(Long id) {
        logger.info("Called approveOrder method: {}", id);

        OrderEntity foundOrderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id not found!"));

        Order foundOrder = orderModelToEntityMapper.toModel(foundOrderEntity);

        if (foundOrder.getStatus() != OrderStatusEnum.NEW)
            throw new OrderCannotBeUpdatedException("To approve order, its status should be 'NEW'! Now its status is "
                    + foundOrder.getStatus() + ".");

        foundOrder.setStatus(OrderStatusEnum.APPROVED);


        OrderEntity savedOrderEntity = orderRepository.save(orderModelToEntityMapper.toEntity(foundOrder));
        orderSender.send(orderModelToEntityMapper.toModel(savedOrderEntity));
    }

    private void addCustomerFromOrderRequestToOrder(OrderRequest request, Order orderToBind) {

        logger.info("Called addCustomerFromOrderRequestToOrder method: {}", request);

        if (customerService.existsByPhoneNumber(request.getCustomerPhoneNumber()))
            orderToBind.setCustomer( customerService.getByPhoneNumber(request.getCustomerPhoneNumber()) );

        else if (request.getCustomerName() != null
                && request.getCustomerSurname() != null
                && request.getCustomerPhoneNumber() != null) {

            Customer customer = customerService.createCustomerFromOrderRequest(request);

            CustomerEntity customerToSave = customerModelToEntityMapper.toEntity(customer);
            CustomerEntity savedCustomer = customerRepository.save(customerToSave);

            orderToBind.setCustomer(customerModelToEntityMapper.toModel(savedCustomer));
        }

        else
            throw new IllegalArgumentException("You must enter registered customer number or all requested registration information.");

    }

    private void setRelationsBetweenOrderItemEntityAndOrderEntity(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
        logger.info("Called setRelationsBetweenOrderItemEntityAndOrderEntity method: {}", orderEntity);
        if (orderEntity != null && orderItemEntities != null && !orderItemEntities.isEmpty()) {
            for (OrderItemEntity itemEntity : orderItemEntities) {
                itemEntity.setOrder(orderEntity); // Устанавливаем связь
            }
            orderEntity.setItems(orderItemEntities);
        }

    }

    private boolean isOrderCanBeUpdated(Order order) {
        return order.getStatus() != OrderStatusEnum.CANCELLED
                && order.getStatus() != OrderStatusEnum.SHIPPED
                && order.getStatus() != OrderStatusEnum.DELIVERED;
    }


}