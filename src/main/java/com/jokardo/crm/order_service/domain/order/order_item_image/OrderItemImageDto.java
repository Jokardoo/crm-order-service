package com.jokardo.crm.order_service.domain.order.order_item_image;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OrderItemImageDto {

    @NotNull(message = "Image must be not null.")
    private MultipartFile file;

}
