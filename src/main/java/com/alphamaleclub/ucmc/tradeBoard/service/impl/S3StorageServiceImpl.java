package com.alphamaleclub.ucmc.tradeBoard.service.impl;

import com.alphamaleclub.ucmc.tradeBoard.service.S3StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class S3StorageServiceImpl implements S3StorageService {

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucketName;

    private final S3Client s3Client;

    @Override
    public String upload(byte[] file, String fileName) throws IOException {


        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType("image/jpeg")
                        .build(),
                RequestBody.fromBytes(file));

        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    @Override
    public void delete(String fileName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
        );
    }

}
