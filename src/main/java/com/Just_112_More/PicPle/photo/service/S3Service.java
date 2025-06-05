package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.dto.GetS3UrlDto;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 s3Client;
    private final AmazonS3 amazonS3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional(readOnly = true)
    public GetS3UrlDto getPostS3Url(Long memberId, String filename) {
        String key = "uploaded-image/" + memberId + "/" + UUID.randomUUID() + "/" + filename;
        //filename (uploded-image + memberID + randomvalue + filename)
        Date expiration = getExpiration();

        //presigned url 생성
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getPostGeneratePresignedUrlRequest(key, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return GetS3UrlDto.builder()
                .preSignedUrl(url.toExternalForm())
                .key(key)
                .build();
    }

    @Transactional(readOnly = true)
    public GetS3UrlDto getGetS3Url(Long memberId, String filename) {
        Date expiration = getExpiration();

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                getGetGeneratePresignedUrlRequest(filename, expiration);

        URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return GetS3UrlDto.builder()
                .preSignedUrl(url.toExternalForm())
                .key(filename)
                .build();
    }

    //post 용 presigned url 생성
    private GeneratePresignedUrlRequest getPostGeneratePresignedUrlRequest(String filename, Date expiration) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest
        = new GeneratePresignedUrlRequest(bucket, filename)
                .withMethod(HttpMethod.PUT)
                .withKey(filename)
                .withExpiration(expiration);
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.PublicRead.toString()
        );
        return generatePresignedUrlRequest;
    }

    //get 용 presigned url 생성
    private GeneratePresignedUrlRequest getGetGeneratePresignedUrlRequest(String filename, Date expiration) {
        return new GeneratePresignedUrlRequest(bucket, filename)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
    }

    private static Date getExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60; //만료시간 한시간
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // Multipartfile 조회메서드
    public InputStream getObjectStream(String key){
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, key);
        S3Object s3Object = amazonS3Client.getObject(getObjectRequest);
        return s3Object.getObjectContent();
    }

    // Multipartfile 업로드 메서드
    public String uploadObject(MultipartFile multipartFile){
        try {
            String uuidPart = UUID.randomUUID().toString().substring(0, 8); // UUID 앞 8글자
            String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("mmssSSS")); // 분초밀리초 7글자
            int randomNum = new Random().nextInt(100); // 0~99 난수
            String randomPart = String.format("%02d", randomNum);
            String key = uuidPart + timePart + randomPart;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            //S3에 업로드
            amazonS3Client.putObject(
              new PutObjectRequest(bucket, key, multipartFile.getInputStream(), metadata)
            );

            return key;
        } catch (IOException e){
            throw new CustomException(ErrorCode.USER_IMAGE_UPLOAD_FAIL);
        }
    }

}
