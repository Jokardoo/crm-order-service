package com.jokardo.crm.order_service.service;

import com.jokardo.crm.order_service.domain.order.order_item_image.OrderItemImage;
import com.jokardo.crm.order_service.exceptions.image.ImageUploadException;
import com.jokardo.crm.order_service.service.props.MinioProperties;
import io.minio.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ImageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    public String uploadOrderItemImage(OrderItemImage orderItemImage) {
        try {
            createBucket();
        }
        catch (Exception e) {
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }

        MultipartFile file = orderItemImage.getFile();

        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new ImageUploadException("Image must have name.");
        }

        String fileName = generateFileName(file);
        InputStream inputStream;

        try {
            inputStream = file.getInputStream();
        }
        catch (Exception e) {
            throw new ImageUploadException("Image upload failed " + e.getMessage());
        }

        saveImage(inputStream, fileName);
        return fileName;
    }

    @SneakyThrows
    private void createBucket() {
        boolean found = minioClient.bucketExists(BucketExistsArgs
                .builder()
                .bucket(minioProperties.getBucket())
                .build());

        if (!found) {
            minioClient.makeBucket(MakeBucketArgs
                    .builder()
                    .bucket(minioProperties.getBucket())
                    .build());
        }
    }

    private String generateFileName(MultipartFile file) {
        String extension = getExtension(file);
        return UUID.randomUUID().toString() + "." + extension;
    }

    // Ищем расширение файла
    private String getExtension(MultipartFile file) {
        if (file.getOriginalFilename() == null || file.getOriginalFilename().length() == 0)
            throw new ImageUploadException("File must have name.");

        return file.getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    @SneakyThrows
    private void saveImage(InputStream inputStream, String fileName) {
        minioClient.putObject(PutObjectArgs.builder()
                        .stream(inputStream, inputStream.available(), -1)
                        .bucket(minioProperties.getBucket())
                        .object(fileName)
                .build());
    }

    @SneakyThrows
    public void deleteImageByImageName(String imageName) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(imageName)
                .build());
    }

}
