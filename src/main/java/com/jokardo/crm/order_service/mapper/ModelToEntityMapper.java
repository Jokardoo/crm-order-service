package com.jokardo.crm.order_service.mapper;

public interface ModelToEntityMapper<M, E> {

    M toModel(E entity);

    E toEntity(M model);
}