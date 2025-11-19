package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.like.repository.LikeRepository;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.PhotoDto;
import com.Just_112_More.PicPle.photo.dto.RecommendRequest;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<PhotoDto> recommendPhotos(RecommendRequest request) {
        // 1. 형용사 태그와 명사 태그 ID를 하나의 리스트로 합침
        List<Long> allTagIds = new ArrayList<>();
        if (request.getAdjectiveTagIds() != null) allTagIds.addAll(request.getAdjectiveTagIds());
        if (request.getNounTagIds() != null) allTagIds.addAll(request.getNounTagIds());

        // 태그 선택이 없으면 빈 리스트 반환 (혹은 전체 랜덤 반환)
        if (allTagIds.isEmpty()) {
            return List.of();
        }

        // 2. 해당 태그를 가진 사진들을 DB에서 조회
        List<Photo> photos = photoRepository.findByTagIdsIn(allTagIds);

        // 3. [랜덤 알고리즘] 리스트를 섞음 (Shuffle)
        Collections.shuffle(photos);

        // 4. 상위 N개
        return photos.stream()
                .limit(5)
                .map(PhotoDto::new)
                .collect(Collectors.toList());
    }
}
