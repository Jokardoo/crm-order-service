package com.jokardo.crm.order_service;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.address.AddressDto;
import com.jokardo.crm.order_service.domain.customer.CustomerEntity;
import com.jokardo.crm.order_service.domain.order.Order;
import com.jokardo.crm.order_service.domain.order.OrderEntity;
import com.jokardo.crm.order_service.domain.order.OrderRequest;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemEntity;
import com.jokardo.crm.order_service.domain.order.order_item.OrderItemRequest;
import com.jokardo.crm.order_service.repository.CustomerRepository;
import com.jokardo.crm.order_service.repository.OrderItemRepository;
import com.jokardo.crm.order_service.repository.OrderRepository;
import com.jokardo.crm.order_service.service.OrderService;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //тк неизвестно, какие порты могут
// быть заняты, пускай наше приложение запускается на случайном порте
@Testcontainers
@TestConfiguration(proxyBeanMethods = false) // чтобы не создавались прокси бины
@ActiveProfiles("test")
class OrderServiceApplicationTests {

	@LocalServerPort // позволяет получить порт приложения
	private int port;

	@Container
	@ServiceConnection //позволяет передать в спринг необходимую конфигурацию
	private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:15.1-alpine"
	);

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderItemRepository orderItemRepository;

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
	public void testOrderCreatedSuccessfully() {
		RestAssured.given()
				.contentType(ContentType.MULTIPART)
				.header("Authorization", "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huIiwiaWQiOjMsInJvbGVzIjoiUk9MRV9VU0VSIiwiaWF0IjoxNzYyOTYxODQ0LCJleHAiOjE3NjI5NjU0NDR9.aiTQxCKC2VWAs7LoEbpxeVDVYaZOxAHd7ZzZw1cvftYjUgJcdi1bfMq1wPIHr0VsVes6fVJKddWNDgIdVtHOaQ")
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
	public void testOnlyUniqueCustomersInDatabase() {
		CustomerEntity customerEntity1 = generateCustomerEntity("John", "Doe", "88888888888");
		CustomerEntity customerEntity2 = generateCustomerEntity("Jon", "Boe", "88888888888");

		customerRepository.save(customerEntity1);

		Assertions.assertThrows(DataIntegrityViolationException.class, () -> customerRepository.save(customerEntity2));
	}

	@Test
	public void testOrderCancelledSuccessfully() {

		List<OrderEntity> ordersBefore = orderRepository.findAll();
		Assertions.assertEquals(0, ordersBefore.size());

		OrderRequest orderRequest = new OrderRequest();

		List<OrderItemRequest> orderItemRequests = new ArrayList<>();
		orderItemRequests.add(generateOrderItemRequest("name", 3, "description"));

		orderRequest.setItems(orderItemRequests);
		orderRequest.setCustomerName("John");
		orderRequest.setCustomerSurname("Doe");
		orderRequest.setCustomerPhoneNumber("+1234567890");
		orderRequest.setDeliveryAddress(generateAddressDto("street", "city", "postalCode"));

		Order savedOrder = orderService.createOrder(orderRequest);


		Assertions.assertNotNull(savedOrder.getId());
		Assertions.assertNotNull(savedOrder.getCreatedAt());
		Assertions.assertNotNull(savedOrder.getStatus());


		List<OrderEntity> ordersAfterCreating = orderRepository.findAll();
		Assertions.assertEquals(1, ordersAfterCreating.size());

		orderService.deleteByOrderId(savedOrder.getId());

		List<OrderEntity> ordersAfterDeleting = orderRepository.findAll();
		Assertions.assertEquals(0, ordersAfterDeleting.size());


	}

	private CustomerEntity generateCustomerEntity(String firstName, String lastName, String phoneNumber) {
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setName(firstName);
		customerEntity.setSurname(lastName);
		customerEntity.setPhoneNumber(phoneNumber);
		return customerEntity;
	}

	private OrderEntity generateOrderEntity(String customerPhoneNumber, String customerName, String customerSurname) {
		OrderEntity orderEntity = new OrderEntity();

		orderEntity.setDeliveryAddress(generateAddress("street", "city", "123432"));
		orderEntity.setCustomer(generateCustomerEntity(customerPhoneNumber, customerName, customerSurname));

		List<OrderItemEntity> orderItems = new ArrayList<>();

		orderItems.add(generateOrderItemEntity("name", "3", "description"));
		orderEntity.setItems(orderItems);

		return orderEntity;
	}

	private OrderItemEntity generateOrderItemEntity(String name, String quantity, String description) {
		OrderItemEntity orderItemEntity = new OrderItemEntity();
		orderItemEntity.setDescription(description);
		orderItemEntity.setProductName(name);
		orderItemEntity.setQuantity(Integer.parseInt(quantity));

		return orderItemEntity;
	}

	private Address generateAddress(String street, String city, String postalCode) {
		Address address = new Address();
		address.setStreet(street);
		address.setCity(city);
		address.setPostalCode(postalCode);

		return address;
	}

	private OrderItemRequest generateOrderItemRequest(String name, int quantity, String description) {
		OrderItemRequest orderItemRequest = new OrderItemRequest();
		orderItemRequest.setName(name);
		orderItemRequest.setQuantity(quantity);
		orderItemRequest.setDescription(description);

		return orderItemRequest;
	}

	private AddressDto generateAddressDto(String street, String city, String postalCode) {
		AddressDto addressDto = new AddressDto();
		addressDto.setStreet(street);
		addressDto.setCity(city);
		addressDto.setPostalCode(postalCode);
		return addressDto;
	}

}
