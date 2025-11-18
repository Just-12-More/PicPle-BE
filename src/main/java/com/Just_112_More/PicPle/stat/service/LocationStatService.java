package com.Just_112_More.PicPle.stat.service;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.domain.PhotoChangedEvent;
import com.Just_112_More.PicPle.photo.dto.PhotosResponseDto;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.photo.service.PhotoService;
import com.Just_112_More.PicPle.stat.domain.LocationStat;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponse;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import com.Just_112_More.PicPle.stat.repository.LocationStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationStatService {

    @Value("${urls.s3}")
    private String s3Url;

    private final LocationStatRepository locationStatRepository;
    private final PhotoService photoService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void uploadStat(String locationLabel, String roadAddress) {
        locationStatRepository.upsertStat(locationLabel, roadAddress);

        // 트랜잭션 커밋후 실행될 이벤트 등록
        applicationEventPublisher.publishEvent(new PhotoChangedEvent(locationLabel));
    }

    public HotPlaceResponseList calculateTop10FromDB() {

        // photoCnt가 높은 순으로 10개 조회
        List<LocationStat> topLocations = locationStatRepository.findTop10ByOrderByPhotoCntDesc();

        log.info("쿼리 결과: {}개", topLocations.size());

        List<HotPlaceResponse> results = new ArrayList<>();

        for(int i = 0; i < topLocations.size(); i++){
            LocationStat locationStat = topLocations.get(i);
            log.info("처리 중: {} / {}", locationStat.getLocationLabel(), locationStat.getRoadAddress());

            // 위치 라벨로 geocoding을 통해 위도, 경도 조회
            if ("도로명 정보 없음".equals(locationStat.getRoadAddress())) {
                log.info("건너뜀: {}", locationStat.getLocationLabel());
                continue;
            }
            List<String> geoData = photoService.geoCoding(locationStat.getRoadAddress());
            log.info("Geo 결과: {}", geoData);
            String latitude = geoData.get(0);  // 위도
            String longitude = geoData.get(1);  // 경도

            HotPlaceResponse hotPlaceResponse = HotPlaceResponse.builder()
                    .order(i + 1)
                    .locationLabel(locationStat.getLocationLabel())
                    .photoCnt(locationStat.getPhotoCnt())
                    .latitude(latitude)
                    .longitude(longitude)
                    .build();
            results.add(hotPlaceResponse);

        }
        log.info("총 결과 개수 = {}", results.size());

        return HotPlaceResponseList.builder()
                .hotplaces(results)
                .build();

    }

    public PhotosResponseDto getLocationPhotos(String location) {
        // 사진 리스트 가져오기
        List<Photo> photos = photoService.getPhotosByLocation(location);
        log.info("연결된 사진: {}개", photos.size());

        List<uploadPhotoDto> photoList = photos.stream()
                .map( photo -> uploadPhotoDto.builder()
                        .id(photo.getId())
                        .title(photo.getPhotoTitle())
                        .imgUrl(s3Url + photo.getPhotoUrl())
                        .description(photo.getPhotoDesc())
                        .nickname(photo.getUser().getUserName())
                        .profileImgUrl(photo.getUser().getProfilePath())
                        .likeCount(photo.getLikeCount())
                        .isLiked(false) // 실제로 로그인한 사용자가 있으면 여기서 체크
                        .address(photo.getLocationLabel())
                        .createdAt(photo.getPhotoCreate().toString())
                        .build()
                )
                .collect(Collectors.toList());

        return new PhotosResponseDto(photoList);
    }
}
