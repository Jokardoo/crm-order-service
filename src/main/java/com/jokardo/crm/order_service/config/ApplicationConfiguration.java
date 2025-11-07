package com.jokardo.crm.order_service.config;

import com.jokardo.crm.order_service.service.props.MinioProperties;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class ApplicationConfiguration {

    private final MinioProperties minioProperties;


    @Bean
    @Profile("dev")
    public com.jokardo.crm.order_service.config.Profile profileDev() {
        com.jokardo.crm.order_service.config.Profile profile = new com.jokardo.crm.order_service.config.Profile();
        profile.setProfile("dev");

        return profile;
    }

    @Bean
    @Profile("admin")
    public com.jokardo.crm.order_service.config.Profile profileTest() {
        com.jokardo.crm.order_service.config.Profile profile = new com.jokardo.crm.order_service.config.Profile();
        profile.setProfile("admin");

        return profile;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioProperties.getUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

}
