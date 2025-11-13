package com.Just_112_More.PicPle.stat.service;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.photo.service.PhotoService;
import com.Just_112_More.PicPle.stat.domain.LocationStat;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponse;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import com.Just_112_More.PicPle.stat.repository.LocationStatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationStatService {

    @Value("${urls.s3}")
    private String s3Url;

    private final LocationStatRepository locationStatRepository;
    private final PhotoService photoService;

    public HotPlaceResponseList getTop10LocationStats() {

        // photoCnt가 높은 순으로 10개 조회
        List<LocationStat> topLocations = locationStatRepository.findTop10ByOrderByPhotoCntDesc();

        List<HotPlaceResponse> results = new ArrayList<>();

        for(LocationStat locationStat : topLocations){
            // 위치 라벨로 geocoding을 통해 위도, 경도 조회
            List<String> geoData = photoService.geoCoding(locationStat.getLocationLabel());
            String latitude = geoData.get(0);  // 위도
            String longitude = geoData.get(1);  // 경도

            // 사진 리스트 가져오기
            List<Photo> photos = photoService.getPhotosByLocation(locationStat.getLocationLabel());

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

            HotPlaceResponse hotPlaceResponse = HotPlaceResponse.builder()
                    .locationLabel(locationStat.getLocationLabel())
                    .photoCnt(locationStat.getPhotoCnt())
                    .latitude(latitude)
                    .longitude(longitude)
                    .photos(photoList)
                    .build();

            results.add(hotPlaceResponse);
        }

        return HotPlaceResponseList.builder()
                .hotplaces(results)
                .build();

    }

}
