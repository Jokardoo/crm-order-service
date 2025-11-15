package com.jokardo.crm.order_service.integration;

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
import com.jokardo.crm.order_service.service.CustomerService;
import com.jokardo.crm.order_service.service.ImageService;
import com.jokardo.crm.order_service.service.OrderService;
import com.jokardo.crm.order_service.service.orderItem.OrderItemService;
import com.jokardo.crm.order_service.util.CustomerBuilder;
import com.jokardo.crm.order_service.util.OrderBuilder;
import com.jokardo.crm.order_service.util.order_item.OrderItemBuilder;
import com.jokardo.crm.order_service.util.order_item.OrderItemRequestBuilder;
import com.jokardo.crm.order_service.util.OrderRequestBuilder;
import com.jokardo.crm.order_service.util.address.AddressBuilder;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false) // чтобы не создавались прокси бины
@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private OrderItemService orderItemService;

    @MockitoBean
    private ImageService imageService;

    @MockitoBean
    private OrderUtil orderUtil;

    @MockitoBean
    private OrderSender orderSender;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    // Mappers - их можно сделать реальными или мокать в зависимости от сложности
    @Autowired(required = false)
    private AddressModelToDtoMapper addressModelToDtoMapper;

    @MockitoBean
    private OrderModelToEntityMapper orderModelToEntityMapper;

    @MockitoBean
    private OrderItemModelToEntityMapper orderItemModelToEntityMapper;

    @MockitoBean
    private CustomerModelToEntityMapper customerModelToEntityMapper;

    @MockitoBean
    private OrderItemModelToDtoMapper orderItemModelToDtoMapper;

    private OrderRequest orderRequest;
    private Order order;
    private OrderEntity orderEntity;
    private Customer customer;
    private CustomerEntity customerEntity;

    private OrderRequestBuilder orderRequestBuilder;

    @BeforeEach
    void setUp() {
        orderRequest = OrderRequestBuilder
                .builder()
                .withCustomer("John", "Doe", "88888888888")
                .withItems( List.of(OrderItemRequestBuilder.buildDefaultValidOrderItemRequest()) )
                .build();

        customer = CustomerBuilder
                .builder()
                .withId(1L)
                .withName("John")
                .withSurname("Doe")
                .withPhoneNumber("88888888888")
                .build();


        customerEntity = new CustomerEntity();
        customerEntity.setId(1L);
        customerEntity.setPhoneNumber("88888888888");
        customerEntity.setName("John");
        customerEntity.setSurname("Doe");


        order = OrderBuilder.builder()
                .withId(1L)
                .withStatus(OrderStatusEnum.NEW)
                .withCreatedAt(LocalDateTime.now())
                .withCustomer(customer)
                .withDeliveryAddress(
                        AddressBuilder.builder()
                                .withCity("city")
                                .withStreet("street")
                                .withPostalCode("123456")
                                .build()
                )
                .build();

        orderEntity = new OrderEntity();
        orderEntity.setId(1L);
        orderEntity.setStatus(OrderStatusEnum.NEW);
        orderEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createOrder_WithValidRequest_ShouldCreateOrder() {
        // Arrange
        when(customerService.existsByPhoneNumber(anyString())).thenReturn(false);
        when(customerService.createCustomerFromOrderRequest(any(OrderRequest.class))).thenReturn(customer);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(customerEntity);
        when(customerModelToEntityMapper.toModel(any(CustomerEntity.class))).thenReturn(customer);

        List<OrderItem> orderItems = List.of(
                OrderItemBuilder.builder()
                        .withDescription("description")
                        .withProductName("product name")
                        .withQuantity(2)
                .build()
        );

        when(orderItemService.parseOrderItemsRequests(anyList())).thenReturn(orderItems);

        List<OrderItemEntity> orderItemEntities = List.of(new OrderItemEntity(), new OrderItemEntity());

        when(orderItemModelToEntityMapper.toEntity(anyList())).thenReturn(orderItemEntities);

        when(orderModelToEntityMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderModelToEntityMapper.toModel(any(OrderEntity.class))).thenReturn(order);

        // Act
        Order result = orderService.createOrder(orderRequest);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(OrderStatusEnum.NEW, result.getStatus());
        Assertions.assertNotNull(result.getCreatedAt());
        Assertions.assertNotNull(result.getCustomer());

        verify(orderRepository, times(1)).save(any(OrderEntity.class));
        verify(customerRepository, times(1)).save(any(CustomerEntity.class));
    }

    @Test
    void createOrder_WithExistingCustomer_ShouldUseExistingCustomer() {
        // Arrange
        when(customerService.existsByPhoneNumber(anyString())).thenReturn(true);
        when(customerService.getByPhoneNumber(anyString())).thenReturn(customer);
        when(orderItemService.parseOrderItemsRequests(anyList())).thenReturn(List.of());
        when(orderItemModelToEntityMapper.toEntity(anyList())).thenReturn(List.of());
        when(orderModelToEntityMapper.toEntity(any(Order.class))).thenReturn(orderEntity);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderModelToEntityMapper.toModel(any(OrderEntity.class))).thenReturn(order);

        // Act
        Order result = orderService.createOrder(orderRequest);

        // Assert
        assertNotNull(result);
        verify(customerService, times(1)).getByPhoneNumber(orderRequest.getCustomerPhoneNumber());
        verify(customerService, never()).createCustomerFromOrderRequest(any());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void createOrder_WithEmptyItems_ShouldThrowException() {
        // Arrange
        OrderRequest invalidRequest = OrderRequestBuilder.builder()
                .withCustomer("John", "Doe", "88888888888")
                .withItems(List.of())
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest));
    }

    @Test
    void createOrder_WithNullItems_ShouldThrowException() {
        // Arrange
        OrderRequest invalidRequest = OrderRequestBuilder.builder()
                .withCustomer("John", "Doe", "88888888888")
                .withItems(null)
                .build();

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest));
    }

    @Test
    void deleteByOrderId_ShouldDeleteOrderAndImages() {
        // Arrange
        Long orderId = 1L;
        List<String> imageNames = List.of("image1.jpg", "image2.jpg");
        when(orderRepository.findImagesNamesByOrderId(orderId)).thenReturn(imageNames);
        doNothing().when(orderRepository).deleteById(orderId);

        // Act
        orderService.deleteByOrderId(orderId);

        // Assert
        verify(imageService, times(1)).deleteImageByImageName("image1.jpg");
        verify(imageService, times(1)).deleteImageByImageName("image2.jpg");
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    void updateOrderInfo_WithUpdatableStatus_ShouldUpdateOrder() {
        // Arrange
        Long orderId = 1L;
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderModelToEntityMapper.toModel(orderEntity)).thenReturn(order);
        doNothing().when(orderUtil).updateAllOrderFields(order, updateRequest);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderModelToEntityMapper.toModel(orderEntity)).thenReturn(order);

        // Act
        Order result = orderService.updateOrderInfo(orderId, updateRequest);

        // Assert
        assertNotNull(result);
        verify(orderUtil, times(1)).updateAllOrderFields(order, updateRequest);
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void updateOrderInfo_WithNonUpdatableStatus_ShouldThrowException() {
        // Arrange
        Long orderId = 1L;
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();

        Order cancelledOrder = OrderBuilder.builder()
                .withId(orderId)
                .withStatus(OrderStatusEnum.CANCELLED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderModelToEntityMapper.toModel(orderEntity)).thenReturn(cancelledOrder);

        // Act & Assert
        assertThrows(OrderCannotBeUpdatedException.class,
                () -> orderService.updateOrderInfo(orderId, updateRequest));

        verify(orderUtil, never()).updateAllOrderFields(any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void updateOrderInfo_WithNonExistingOrder_ShouldThrowException() {
        // Arrange
        Long orderId = 999L;
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderInfo(orderId, updateRequest));
    }

    @Test
    void approveOrder_WithNewStatus_ShouldApproveAndSend() {
        // Arrange
        Long orderId = 1L;

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderModelToEntityMapper.toModel(orderEntity)).thenReturn(order);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(orderEntity);
        when(orderModelToEntityMapper.toModel(orderEntity)).thenReturn(order);

        // Act
        orderService.approveOrder(orderId);

        // Assert
        assertEquals(OrderStatusEnum.APPROVED, order.getStatus());
        verify(orderRepository, times(1)).save(orderEntity);
        verify(orderSender, times(1)).send(order);
    }

    @Test
    void approveOrder_WithNonNewStatus_ShouldThrowException() {
        // Arrange
        Long orderId = 1L;
        Order approvedOrder = OrderBuilder.builder()
                .withId(orderId)
                .withStatus(OrderStatusEnum.APPROVED)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(orderEntity));
        when(orderModelToEntityMapper.toModel(orderEntity)).thenReturn(approvedOrder);


        assertThrows(OrderCannotBeUpdatedException.class,
                () -> orderService.approveOrder(orderId));

        verify(orderRepository, never()).save(any());
        verify(orderSender, never()).send(any());
    }

    @Test
    void approveOrder_WithNonExistingOrder_ShouldThrowException() {
        // Arrange
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> orderService.approveOrder(orderId));
    }

    @Test
    void createOrder_WithInsufficientCustomerInfo_ShouldThrowException() {
        // Arrange
        OrderRequest invalidRequest = OrderRequestBuilder.builder()
                .withCustomer("John", null, null)  // No phone number // Only name, no surname
                .withItems(List.of(OrderItemRequestBuilder
                        .buildDefaultValidOrderItemRequest()
                    )
                ).build();

        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest));
    }

    @Test
    void createOrder_WithNullPhoneNumber_ShouldThrowException() {

        OrderRequest invalidRequest = OrderRequestBuilder.builder()
                .withCustomer("John", "Doe", null)  // No phone number // Only name, no surname
                .withItems(List.of(OrderItemRequestBuilder
                                .buildDefaultValidOrderItemRequest()
                        )
                ).build();


        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> orderService.createOrder(invalidRequest));
    }
}