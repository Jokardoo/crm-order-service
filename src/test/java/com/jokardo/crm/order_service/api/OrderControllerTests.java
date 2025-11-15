package com.jokardo.crm.order_service.api;

import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderRequest;
import com.jokardo.crm.order_service.domain.order.OrderUpdateRequest;
import com.jokardo.crm.order_service.repository.CustomerRepository;
import com.jokardo.crm.order_service.repository.OrderRepository;
import com.jokardo.crm.order_service.service.OrderService;
import com.jokardo.crm.order_service.util.address.AddressDtoBuilder;
import com.jokardo.crm.order_service.util.CustomerBuilder;
import com.jokardo.crm.order_service.util.order_item.OrderItemRequestBuilder;
import com.jokardo.crm.order_service.util.OrderRequestBuilder;
import com.jokardo.crm.order_service.util.jwtTokenGenerator.JwtAccessTokenGenerator;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //тк неизвестно, какие порты могут
@ActiveProfiles("test")
@TestConfiguration(proxyBeanMethods = false) // чтобы не создавались прокси бины
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class OrderControllerTests {

    private final OrderRequestBuilder orderRequestBuilder;
    private final OrderService orderService;

    @LocalServerPort // позволяет получить порт приложения
    private int port;

    @Autowired
    private JwtAccessTokenGenerator accessTokenGenerator;

    @Container
    @ServiceConnection //позволяет передать в спринг необходимую конфигурацию
    private final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15.1-alpine"
    );

    @Autowired
    private OrderRepository orderRepository;


    @Autowired
    private CustomerRepository customerRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        orderRepository.deleteAll();	// отчищаем полностью базу, чтобы не пересоздавать заново контейнер
        customerRepository.deleteAll();
    }

    //TODO нужно автоматически генерировать токен
    @Test
    public void createOrder_WithValidData_ShouldReturnCreated() {
        RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .header("Authorization", "Bearer " + accessTokenGenerator.createUserAccessToken())
                .multiPart("items[0].name", "шкаф")
                .multiPart("items[0].quantity", 2)
                .multiPart("items[0].description", "шкаф")
                .multiPart("customerPhoneNumber", "+1234567890")
                .multiPart("customerName", "John")
                .multiPart("customerSurname", "Doe")
                .multiPart("deliveryAddress.street", "Main Street")
                .multiPart("deliveryAddress.city", "New York")
                .multiPart("deliveryAddress.postalCode", "10001")
                .when()
                .post("/v1/orders/create")

                .then()
                .statusCode(HttpStatus.CREATED.value()
                );
    }

    @Test
    public void createOrder_WithInvalidData_ShouldReturnBadRequest() {
        RestAssured.given()
                .contentType(ContentType.MULTIPART)
                .header("Authorization", "Bearer " + accessTokenGenerator.createUserAccessToken())
                .multiPart("items[0].name", "шкаф")
                .multiPart("items[0].quantity", 2)
                .multiPart("items[0].description", "шкаф")
                .multiPart("customerPhoneNumber", "")
                .multiPart("customerName", "John")
                .multiPart("customerSurname", "Doe")
                .multiPart("deliveryAddress.street", "Main Street")
                .multiPart("deliveryAddress.city", "New York")
                .multiPart("deliveryAddress.postalCode", "10001")
                .when()
                .post("/v1/orders/create")

                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value()
                );
    }

    @Test
    void deleteOrder_WithValidId_ShouldRemoveOrderFromDatabase() {

        OrderRequest orderRequest = getOrderRequestWithValidData();

        Order savedOrder = orderService.createOrder(orderRequest);
        Long orderId = savedOrder.getId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessTokenGenerator.createUserAccessToken())
                .when().delete("/v1/orders/{id}/delete", orderId)
                .then()
                .statusCode(HttpStatus.OK.value());



    }

    @Test
    public void updateOder_NoOrderInDatabase_ShouldNotUpdateOrder() {

        OrderUpdateRequest orderUpdateRequest = new OrderUpdateRequest();

        orderUpdateRequest.setDeliveryAddress(
                AddressDtoBuilder.builder().getDefaultValidAddressDto()
        );

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessTokenGenerator.createUserAccessToken())
                .body(orderUpdateRequest)
                .when()
                .patch("/vr/orders/1/update")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());

    }

    // Запрос поступает от юзера
    @Test
    public void approveOrder_WithValidData_ShouldNotApproveOrder() {
        OrderRequest orderRequest = getOrderRequestWithValidData();
        Long savedOrderId = orderService.createOrder(orderRequest).getId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessTokenGenerator.createUserAccessToken())
                .when()
                .post("/v1/orders/{id}/approve", savedOrderId)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());

    }

    // Запрос поступает от админа
    @Test
    public void approveOrder_WithValidData_ShouldApproveOrder() {
        OrderRequest orderRequest = getOrderRequestWithValidData();
        Long savedOrderId = orderService.createOrder(orderRequest).getId();

        RestAssured.given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + accessTokenGenerator.createAdminAccessToken())
                .when()
                .post("/v1/orders/{id}/approve", savedOrderId)
                .then()
                .statusCode(HttpStatus.OK.value());

    }

    private OrderRequest getOrderRequestWithValidData() {
        Customer customer = CustomerBuilder.generateDefaultValidCustomer();

        return OrderRequestBuilder.builder()
                .withCustomer(
                        customer.getName(),
                        customer.getSurname(),
                        customer.getPhoneNumber()
                )
                .withItems(
                        List.of( OrderItemRequestBuilder.buildDefaultValidOrderItemRequest() )
                )
                .build();

    }



}
