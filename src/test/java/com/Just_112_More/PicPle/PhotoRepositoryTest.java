package com.Just_112_More.PicPle;

import com.Just_112_More.PicPle.contatiner.MySqlRepositoryTest;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PhotoRepository.class)
class PhotoRepositoryTest extends MySqlRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    @DisplayName("Bounding Box + Haversine 필터링 통합 테스트: 정확도 검증")
    void testLocationBasedSearch() {
        // 1. 테스트용 기준 위치 (서울 강남역 근처)
        double centerLat = 37.4979;
        double centerLon = 127.0276;
        double radiusKm = 1.0; // 1km 이내 검색

        // 2. 데이터 준비
        Photo inside = Photo.builder()
                .latitude(37.5000).longitude(127.0300)
                .photoTitle("가까운 사진(반경 내)")
                .photoUrl("dummy-url-1")
                .build();

        Photo edgeOut = Photo.builder()
                .latitude(37.5060).longitude(127.0380)
                .photoTitle("모서리 밖 사진(사각형 안/원 밖)")
                .photoUrl("dummy-url-2")
                .build();

        Photo farAway = Photo.builder()
                .latitude(37.6000).longitude(127.1000)
                .photoTitle("먼 사진(완전 외부)")
                .photoUrl("dummy-url-3")
                .build();

        em.persist(inside);
        em.persist(edgeOut);
        em.persist(farAway);
        em.flush();
        em.clear();

        // 3. 메서드 실행
        List<Photo> results = photoRepository.findPhotosByLocation(centerLat, centerLon, radiusKm);

        // 4. 검증 및 상세 결과 출력
        System.out.println("\n--- [검색 결과 리포트] ---");
        System.out.println("기준 좌표: (" + centerLat + ", " + centerLon + ") / 반경: " + radiusKm + "km");
        System.out.println("조회된 사진 수: " + results.size());

        for (Photo p : results) {
            double actualDist = calculateHaversine(centerLat, centerLon, p.getLatitude(), p.getLongitude());
            System.out.println("▶ [" + p.getPhotoTitle() + "] 실제 거리: " + String.format("%.3f", actualDist) + "km");
        }
        System.out.println("--------------------------\n");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPhotoTitle()).isEqualTo("가까운 사진(반경 내)");
    }

    @Test
    @DisplayName("대량 데이터 성능 비교를 위한 기초 테스트")
    void performanceCheck() {
        for (int i = 0; i < 2000; i++) {
            em.persist(Photo.builder()
                    .latitude(37.49 + (Math.random() * 0.05))
                    .longitude(127.02 + (Math.random() * 0.05))
                    .photoTitle("Random Photo " + i)
                    .photoUrl("url")
                    .build());
        }
        em.flush();

        long startTime = System.currentTimeMillis();
        List<Photo> results = photoRepository.findPhotosByLocation(37.4979, 127.0276, 1.0);
        long endTime = System.currentTimeMillis();

        System.out.println("\n⏱ [성능 체크] 2000건 중 " + results.size() + "건 조회 소요 시간: " + (endTime - startTime) + "ms\n");
    }

    // 결과 해석을 돕기 위한 보조 메서드 (Java 공식 계산)
    private double calculateHaversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}