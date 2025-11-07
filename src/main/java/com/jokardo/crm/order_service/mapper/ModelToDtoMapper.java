package com.jokardo.crm.order_service.mapper;

public interface ModelToDtoMapper <M, D> {

    D toDto(M model);

    M toModel(D dto);

}