package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.like.repository.LikeRepository;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhotoService {
    @Value("${naver.x-ncp-apigw-api-key-id}")
    private String naverKey;

    @Value("${naver.x-ncp-apigw-api-key}")
    private String naverPw;

    private final PhotoRepository photoRepository;
    private final LikeRepository likeRepository;

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

                    // (1) 도로명 주소 파싱 (roadAddress)
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
                            reverseGeoCoding.add(roadAddress);
                        }
                    }

                    // (2) 일반 법정동 주소 (defaultAddress)
                    if ("admcode".equals(name) || "addr".equals(name)) {
                        JsonNode region = node.path("region");
                        String area1 = region.path("area1").path("name").asText("");
                        String area2 = region.path("area2").path("name").asText("");
                        String area3 = region.path("area3").path("name").asText("");
                        //String area4 = region.path("area4").path("name").asText("");
                        defaultAddress = String.join(" ", area1, area2, area3).trim();
                        reverseGeoCoding.add(defaultAddress);
                    }
                }
            }

            if (reverseGeoCoding.isEmpty()) {
                reverseGeoCoding.add(
                        roadAddress != null ? roadAddress : "도로명 정보 없음"
                );
                reverseGeoCoding.add(
                        defaultAddress != null ? defaultAddress : "주소 정보 없음"
                );
            }

            return reverseGeoCoding;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(
                    roadAddress != null ? roadAddress : "도로명 정보 없음",
                    defaultAddress != null ? defaultAddress : "주소 정보 없음"
            );
        }
    }

    public List<String> geoCoding(String localLabel){
        List<String> locationResult = new ArrayList<>();

        String requestUrl = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode"
                + "?query=" + localLabel
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
                String latitude = address.path("y").asText(); // 위도
                String longitude = address.path("x").asText(); // 경도

                // 위도와 경도 반환
                locationResult.add(latitude);
                locationResult.add(longitude);

                return locationResult;
            }

            // 위치 정보 없음 반환
            locationResult.add("위치 정보 없음");
            return locationResult;

        } catch (Exception e) {
            e.printStackTrace();

            // 예외 발생 시 실패 메시지 추가
            locationResult.add("위치 정보 파싱 실패");
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
}
