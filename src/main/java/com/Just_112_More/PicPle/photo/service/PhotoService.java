package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.like.repository.LikeRepository;
import com.Just_112_More.PicPle.photo.domain.Photo;
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

@Service
@RequiredArgsConstructor
public class PhotoService {
    @Value("${naver.x-ncp-apigw-api-key-id}")
    private String naverKey;

    @Value("${naver.x-ncp-apigw-api-key}")
    private String naverPw;

    private final PhotoRepository photoRepository;
    private final LikeRepository likeRepository;

    public String geoCoding(Double lat, Double lon) {
        String requestUrl = "https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc"
                + "?request=coordsToaddr"
                + "&coords=" + lon + "," + lat
                + "&sourcecrs=epsg:4326"
                + "&orders=admcode,addr"
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
                // "addr" 또는 "roadaddr" 우선 사용
                for (JsonNode node : results) {
                    String name = node.path("name").asText();
                    if ("admcode".equals(name) || "addr".equals(name)) {
                        JsonNode region = node.path("region");
                        String area1 = region.path("area1").path("name").asText("");
                        String area2 = region.path("area2").path("name").asText("");
                        String area3 = region.path("area3").path("name").asText("");
                        //String area4 = region.path("area4").path("name").asText("");
                        return String.join(" ", area1, area2, area3).trim();
                    }
                }
            }

            return "주소 정보 없음";

        } catch (Exception e) {
            e.printStackTrace();
            return "주소 파싱 실패";
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
}
