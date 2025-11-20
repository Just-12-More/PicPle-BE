package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.like.repository.LikeRepository;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.UploadPhotoRequestDto;
import com.Just_112_More.PicPle.photo.domain.PhotoChangedEvent;
import com.Just_112_More.PicPle.photo.domain.Tag;
import com.Just_112_More.PicPle.photo.dto.*;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
import com.Just_112_More.PicPle.photo.repository.TagRepository;
import com.Just_112_More.PicPle.user.domain.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {
    @Value("${naver.x-ncp-apigw-api-key-id}")
    private String naverKey;

    @Value("${naver.x-ncp-apigw-api-key}")
    private String naverPw;

    @Value("${urls.s3}")
    private String s3Url;
    private final PhotoRepository photoRepository;
    private final LikeRepository likeRepository;
    private final TagRepository tagRepository;

    @Transactional
    public Photo uploadPhoto(UploadPhotoRequestDto requestDto, User user) {
        Photo photo = Photo.builder()
                .photoTitle(requestDto.getTitle())
                .photoDesc(requestDto.getDescription())
                .photoUrl(requestDto.getPhotoUrl())
                .latitude(requestDto.getLatitude())
                .longitude(requestDto.getLongitude())

                //.roadAddress(addressList.get(0))
                //.locationLabel(addressList.get(1))
                .build();
        photo.setUser(user);
        this.addTags(photo, requestDto.getTagIds());
        return photoRepository.save(photo);
    }

    public List<String> reverseGeoCoding(Double lat, Double lon) {
        List<String> reverseGeoCoding = new ArrayList<>();
        String roadAddress = null; // 도로명 주소
        String defaultAddress = null; // 일반 주소 (fallback)

        String requestUrl = "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc"
                + "?request=coordsToaddr"
                + "&coords=" + lon + "," + lat
                + "&sourcecrs=epsg:4326"
                + "&orders=admcode,addr,roadaddr"
                + "&output=json";

        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(requestUrl);

            request.addHeader("x-ncp-apigw-api-key-id", naverKey);
            request.addHeader("x-ncp-apigw-api-key", naverPw);

            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(result.toString());

            JsonNode results = root.path("results");
            if (results.isArray() && results.size() > 0) {

                for (JsonNode node : results) {
                    String name = node.path("name").asText();

                    // (1) 일반 법정동 주소 (defaultAddress)
                    if ("admcode".equals(name) ) {
                        JsonNode region = node.path("region");
                        String area1 = region.path("area1").path("name").asText("");
                        String area2 = region.path("area2").path("name").asText("");
                        String area3 = region.path("area3").path("name").asText("");
                        //String area4 = region.path("area4").path("name").asText("");
                        defaultAddress = String.join(" ", area1, area2, area3).trim();
                    }

                    // (2) 도로명 주소 파싱 (roadAddress)
                    if ("roadaddr".equals(name)) {
                        JsonNode region = node.path("region");
                        JsonNode land = node.path("land");

                        String area1 = region.path("area1").path("name").asText("");
                        String area2 = region.path("area2").path("name").asText("");
                        String roadName = land.path("name").asText("");
                        String number1 = land.path("number1").asText("");

                        if (!area1.isEmpty() && !area2.isEmpty() && !roadName.isEmpty()) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(area1).append(" ")
                                    .append(area2).append(" ")
                                    .append(roadName)
                                    .append(number1);
                            roadAddress = sb.toString().trim();
                        }
                    }
                }
            }

            reverseGeoCoding.add(roadAddress != null ? roadAddress : "도로명 정보 없음");
            reverseGeoCoding.add(defaultAddress != null ? defaultAddress : "주소 정보 없음");
            return reverseGeoCoding;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of("도로명 정보 없음", "주소 정보 없음");
        }
    }

    public List<String> geoCoding(String roadAddress) {
        List<String> locationResult = new ArrayList<>();

        String encodedAddress = URLEncoder.encode(roadAddress, StandardCharsets.UTF_8);
        String requestUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode"
                + "?query=" + encodedAddress
                + "&output=json";

        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(requestUrl);

            request.addHeader("x-ncp-apigw-api-key-id", naverKey);
            request.addHeader("x-ncp-apigw-api-key", naverPw);

            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            reader.close();

            // JSON 파싱
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(result.toString());

            JsonNode results = root.path("addresses");
            if (results.isArray() && results.size() > 0) {
                JsonNode address = results.get(0);

                String longitude = address.path("x").asText();  // 경도
                String latitude = address.path("y").asText();   // 위도

                // 위도와 경도 반환
                locationResult.add(latitude);   // [0] 위도
                locationResult.add(longitude);  // [1] 경도

                return locationResult;
            }

            // 주소가 없을 때
            locationResult.add("0.0");  // 위도 없음
            locationResult.add("0.0");  // 경도 없음

            return locationResult;

        } catch (Exception e) {
            e.printStackTrace();

            // 예외 발생 시
            locationResult.add("0.0");
            locationResult.add("0.0");
            return locationResult;
        }
    }

    @Transactional
    public void addLike(Photo photo, User user) {
        Like like = new Like();
        like.setPhoto(photo);
        like.setUser(user);
        likeRepository.save(like);

        photo.addLike(like);
        photoRepository.save(photo);
    }

    public List<Photo> getPhotosByLocation(String locationLabel) {
        return photoRepository.findByLocationLabel(locationLabel);
    }

    @Transactional
    public void updatePhotoAddress(Long photoId, String locationLabel, String roadAddress){
        Photo photo = photoRepository.getPhotoById(photoId);
        if (photo == null) throw new CustomException(ErrorCode.PHOTO_NOT_FOUND);
        photo.updateAddress(locationLabel, roadAddress);
    }
  
    public List<PhotoDto> recommendPhotos(RecommendRequest request) {
        List<Long> tagIds = request.getTagIds();

        if (tagIds==null || tagIds.isEmpty()) {
            return List.of();
        }

        List<Photo> photos = photoRepository.findByTagIdsIn(tagIds);
        Collections.shuffle(photos);

        // 상위 N개
        return photos.stream()
                .map(photo -> new PhotoDto(photo, s3Url))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTags(Photo photo, List<Long> tagIds) {
        if(tagIds==null || tagIds.isEmpty()) return;
        List<Tag> tags = tagRepository.findByIds(tagIds);
        photo.addTags(tags);
    }

    public List<HotTagDto> getHotTags() {
        return photoRepository.countPhotoTags();
    }
}
