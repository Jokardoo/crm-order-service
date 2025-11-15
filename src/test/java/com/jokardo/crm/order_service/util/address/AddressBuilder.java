package com.jokardo.crm.order_service.util.address;

import com.jokardo.crm.order_service.domain.address.Address;
import com.jokardo.crm.order_service.domain.address.AddressDto;

public class AddressBuilder {
    private String city;
    private String street;
    private String postalCode;

    public static AddressBuilder builder() {
        return new AddressBuilder();
    }

    public AddressBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AddressBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public AddressBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public Address build() {
        Address address = new Address();
        address.setCity(city);
        address.setStreet(street);
        address.setPostalCode(postalCode);
        return address;
    }

    public Address getDefaultValidAddressDto() {
        Address address = new Address();
        address.setCity("San Francisco");
        address.setStreet("19 street");
        address.setPostalCode("9410");

        return address;
    }
}
