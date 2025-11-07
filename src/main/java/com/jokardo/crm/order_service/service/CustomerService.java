package com.jokardo.crm.order_service.service;

import com.jokardo.crm.order_service.domain.customer.Customer;
import com.jokardo.crm.order_service.domain.customer.CustomerEntity;
import com.jokardo.crm.order_service.domain.order.OrderRequest;
import com.jokardo.crm.order_service.exceptions.customer.CustomerNotFoundException;
import com.jokardo.crm.order_service.exceptions.customer.CustomerPhoneNumberAlreadyExistsException;
import com.jokardo.crm.order_service.mapper.customer.CustomerModelToEntityMapper;
import com.jokardo.crm.order_service.mapper.customer.OrderRequestToCustomerMapper;
import com.jokardo.crm.order_service.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerModelToEntityMapper customerModelToEntityMapper;
    private final OrderRequestToCustomerMapper orderRequestToCustomerMapper;

    @Transactional
    public boolean existsByPhoneNumber(String phoneNumber) {
        return customerRepository.existsByPhoneNumber(phoneNumber);
    }

    @Transactional
    public Customer getByPhoneNumber(String phoneNumber) {

        log.info("Called method getByPhoneNumber: {}", phoneNumber);

        CustomerEntity entity = customerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with phone number '" + phoneNumber + "' not found!"));

        return customerModelToEntityMapper.toModel(entity);
    }

    @Transactional
    public Customer createCustomerFromOrderRequest(OrderRequest request) {

        log.info("Called method createCustomerFromOrderRequest: {}", request);

        if (request == null)
            throw  new IllegalArgumentException("Request cannot be null!");

        if (customerRepository.existsByPhoneNumber(request.getCustomerPhoneNumber()))
            throw new CustomerPhoneNumberAlreadyExistsException("Customer with phone number '"
                    + request.getCustomerPhoneNumber() + "' already exists!");


        Customer customerToCreate = orderRequestToCustomerMapper.toCustomer(request);

        CustomerEntity customerToSave = customerModelToEntityMapper.toEntity(customerToCreate);
        CustomerEntity savedCustomer = customerRepository.save(customerToSave);

        return customerModelToEntityMapper.toModel(savedCustomer);
    }
}
