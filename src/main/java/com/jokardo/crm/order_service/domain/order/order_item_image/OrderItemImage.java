package com.jokardo.crm.order_service.domain.order.order_item_image;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OrderItemImage {
    
    private MultipartFile file;
    
}
