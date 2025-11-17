package com.jokardo.crm.order_service.integration;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.customer.CustomerEntity;
import com.jokardo.crm.order_service.domain.order.*;
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
import com.jokardo.crm.order_service.util.address.AddressDtoBuilder;
import com.jokardo.crm.order_service.util.order_item.OrderItemRequestBuilder;
import com.jokardo.crm.order_service.util.OrderRequestBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.Assert.*;

@Testcontainers
@TestConfiguration(proxyBeanMethods = false) // чтобы не создавались прокси бины
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //тк неизвестно, какие порты могут
@ActiveProfiles("test")
public class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @Container
    @ServiceConnection //позволяет передать в спринг необходимую конфигурацию
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.1-alpine"
    );

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderItemService orderItemService;

    @MockitoBean
    private ImageService imageService;

    @Autowired
    private OrderUtil orderUtil;
    @Autowired
    private OrderSender orderSender;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    // Mappers - их можно сделать реальными или мокать в зависимости от сложности
    @Autowired(required = false)
    private AddressModelToDtoMapper addressModelToDtoMapper;

    @Autowired
    private OrderModelToEntityMapper orderModelToEntityMapper;

    @Autowired
    private OrderItemModelToEntityMapper orderItemModelToEntityMapper;

    @Autowired
    private CustomerModelToEntityMapper customerModelToEntityMapper;

    @Autowired
    private OrderItemModelToDtoMapper orderItemModelToDtoMapper;

    @Autowired(required = false)
    private OrderRequest orderRequest;
    @Autowired(required = false)
    private Order order;
    @Autowired(required = false)
    private OrderEntity orderEntity;
    @Autowired(required = false)
    private Customer customer;
    @Autowired(required = false)
    private CustomerEntity customerEntity;

    private OrderRequestBuilder orderRequestBuilder;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        orderRepository.deleteAll();
        orderItemRepository.deleteAll();

    }

    @Test
    void createOrder_WithValidRequest_ShouldCreateOrder() {

        Customer customer = CustomerBuilder.generateDefaultValidCustomer();

        OrderRequest orderRequest = OrderRequestBuilder
                .builder()
                .withCustomer(
                        customer.getName(),
                        customer.getSurname(),
                        customer.getPhoneNumber()
                )
                .withItems( List.of(OrderItemRequestBuilder.buildDefaultValidOrderItemRequest()) )
                .build();


        Assertions.assertFalse( customerRepository.findByPhoneNumber( customer.getPhoneNumber() )
                .isPresent());

        Assertions.assertTrue( orderRepository.findByCustomerPhoneNumber( customer.getPhoneNumber() )
                .isEmpty());

        Order createdOrder = orderService.createOrder(orderRequest);

        Assertions.assertTrue(orderRepository.findByCustomerPhoneNumber(customer.getPhoneNumber()).size() == 1);
        Assertions.assertTrue(customerRepository.findByPhoneNumber( customer.getPhoneNumber() ).isPresent());

        Assertions.assertTrue(createdOrder.getStatus() == OrderStatusEnum.NEW);

        Address savedAddress = new Address();
        savedAddress.setCity(orderRequest.getDeliveryAddress().getCity());
        savedAddress.setStreet(orderRequest.getDeliveryAddress().getStreet());
        savedAddress.setPostalCode(orderRequest.getDeliveryAddress().getPostalCode());

        Assertions.assertEquals(createdOrder.getDeliveryAddress(), savedAddress);
        Assertions.assertNotNull(createdOrder.getCreatedAt());


    }

    @Test
    void createOrder_WithExistingCustomer_WithAnotherNameAndSurname_ShouldThrowIllegalArgumentException() {
        Customer existingCustomer = CustomerBuilder.generateDefaultValidCustomer();

        customerRepository.save(customerModelToEntityMapper.toEntity(existingCustomer));

        Assertions.assertNotNull(orderRepository.findByCustomerPhoneNumber(existingCustomer.getPhoneNumber()));

        OrderRequest orderRequest = OrderRequestBuilder.builder()
                .withItems(
                        List.of(OrderItemRequestBuilder
                                .buildDefaultValidOrderItemRequest())
                )
                // добавляем другого пользователя, который использует зарегестрированный номер
                .withCustomer("Steave", "Belik", existingCustomer.getPhoneNumber())
                .build();

        // Act
        Assertions.assertThrows(IllegalArgumentException.class, () -> orderService.createOrder(orderRequest));

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
    void updateOrderInfo_WithUpdatableStatus_ShouldUpdateOrder() {
        // Arrange
        OrderRequest orderRequest = OrderRequestBuilder
                .builder()
                .withCustomer(
                        CustomerBuilder.generateDefaultValidCustomer()
                )
                .withItems( List.of(OrderItemRequestBuilder.buildDefaultValidOrderItemRequest()) )
                .build();

        Order createdOrder = orderService.createOrder(orderRequest);

        Assertions.assertTrue(createdOrder.getStatus() == OrderStatusEnum.NEW);

        OrderUpdateRequest updateRequest = new OrderUpdateRequest();
        updateRequest.setDeliveryAddress(AddressDtoBuilder.builder().getDefaultValidAddressDto());

        try {
            orderService.updateOrderInfo(createdOrder.getId(), updateRequest);
        } catch (OrderCannotBeUpdatedException e) {
            Assertions.fail();
        }
        Assertions.assertTrue(true);

    }

    @Test
    void updateOrderInfo_WithNonUpdatableStatus_ShouldThrowException() {

        OrderUpdateRequest updateRequest = new OrderUpdateRequest();


        OrderRequest orderRequest = OrderRequestBuilder
                .builder()
                .withCustomer(
                        CustomerBuilder.generateDefaultValidCustomer()
                )
                .withItems( List.of(OrderItemRequestBuilder.buildDefaultValidOrderItemRequest()) )
                .build();

        Order createdOrder = orderService.createOrder(orderRequest);

        Assertions.assertNotNull(createdOrder);

        orderService.cancelOrderById(createdOrder.getId());


        Assertions.assertThrows(OrderCannotBeUpdatedException.class, () -> orderService.updateOrderInfo(createdOrder.getId(), updateRequest));

    }

    @Test
    void updateOrderInfo_WithNonExistingOrder_ShouldThrowException() {
        // Arrange
        Long orderId = 999L;
        OrderUpdateRequest updateRequest = new OrderUpdateRequest();

        // Act & Assert
        assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrderInfo(orderId, updateRequest));
    }

    @Test
    void approveOrder_WithNewStatus_ShouldApproveAndSend() {

        Customer customer = CustomerBuilder.generateDefaultValidCustomer();

        OrderRequest orderRequest = OrderRequestBuilder.builder()
                .withDeliveryAddress(AddressDtoBuilder.builder()
                        .getDefaultValidAddressDto()
                )
                .withCustomer(customer.getName(), customer.getSurname(), customer.getPhoneNumber())
                .withItems(List.of(OrderItemRequestBuilder.buildDefaultValidOrderItemRequest()))
                .build();

        Order savedOrder = orderService.createOrder(orderRequest);

        Assertions.assertNotNull(savedOrder);
        Assertions.assertNotNull(savedOrder.getCustomer());
        Assertions.assertNotNull(savedOrder.getDeliveryAddress());
        Assertions.assertNotNull(savedOrder.getId());
        Assertions.assertEquals(OrderStatusEnum.NEW, savedOrder.getStatus());

        orderService.approveOrder(savedOrder.getId());
        Order approvedOrder = orderService.getOrderById(savedOrder.getId());

        // Assert
        Assertions.assertEquals(OrderStatusEnum.APPROVED, approvedOrder.getStatus());

    }

    @Test
    void approveOrder_WithNonNewStatus_ShouldThrowException() {

        Customer customer = CustomerBuilder.generateDefaultValidCustomer();

        OrderRequest orderRequest = OrderRequestBuilder.builder()
                .withDeliveryAddress(AddressDtoBuilder.builder()
                        .getDefaultValidAddressDto()
                )
                .withCustomer(customer.getName(), customer.getSurname(), customer.getPhoneNumber())
                .withItems(List.of(OrderItemRequestBuilder.buildDefaultValidOrderItemRequest()))
                .build();

        Order createdOrder = orderService.createOrder(orderRequest);
        // подтверждаем заказ в 1й раз
        orderService.approveOrder(createdOrder.getId());

        // Пытаемся подтвердить заказ во второй раз
        Assertions.assertThrows(OrderCannotBeUpdatedException.class, () -> orderService.approveOrder(createdOrder.getId()));

    }

    @Test
    void approveOrder_WithNonExistingOrder_ShouldThrowException() {
        // Arrange
        Long orderId = 999L;

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