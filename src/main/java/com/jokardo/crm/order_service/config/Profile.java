package com.jokardo.crm.order_service.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;

@Data
public class Profile {
    private String profile;

    @PostConstruct
    public void init() {
        System.out.println("Profile initialized");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Profile destroyed");
    }
}
