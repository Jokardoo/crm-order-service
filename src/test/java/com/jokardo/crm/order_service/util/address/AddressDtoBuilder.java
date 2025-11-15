package com.jokardo.crm.order_service.util.address;

import com.jokardo.crm.order_service.domain.address.AddressDto;
import org.springframework.stereotype.Component;

@Component
public class AddressDtoBuilder {
    private String city;
    private String street;
    private String postalCode;

    public static AddressDtoBuilder builder() {
        return new AddressDtoBuilder();
    }

    public AddressDtoBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public AddressDtoBuilder withStreet(String street) {
        this.street = street;
        return this;
    }

    public AddressDtoBuilder withPostalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    public AddressDto build() {
        AddressDto addressDto = new AddressDto();
        addressDto.setCity(city);
        addressDto.setStreet(street);
        addressDto.setPostalCode(postalCode);
        return addressDto;
    }

    public AddressDto getDefaultValidAddressDto() {
        AddressDto addressDto = new AddressDto();

        addressDto.setCity("San Francisco");
        addressDto.setStreet("19 street");
        addressDto.setPostalCode("9410");

        return addressDto;
    }
}
