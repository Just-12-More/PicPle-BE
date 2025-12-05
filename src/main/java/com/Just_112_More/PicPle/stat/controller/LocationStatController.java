package com.Just_112_More.PicPle.stat.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.photo.dto.PhotosResponseDto;
import com.Just_112_More.PicPle.stat.domain.LocationStat;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import com.Just_112_More.PicPle.stat.service.HotPlaceService;
import com.Just_112_More.PicPle.stat.service.LocationStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/stat")
@RequiredArgsConstructor
public class LocationStatController {
    private final LocationStatService locationStatService;
    private final HotPlaceService hotPlaceService;

    /*
    @GetMapping("/top10")
    public ResponseEntity<ApiResponse<?>> getTop10LocationStats() {
        HotPlaceResponseList top10LocationStats = hotPlaceService.getTop10Cache();
        return ResponseEntity.ok(ApiResponse.success(top10LocationStats));
    }
     */

    @GetMapping("/top10")
    public ResponseEntity<ApiResponse<?>> getTop10LocationStats() {
        HotPlaceResponseList top10LocationStats = locationStatService.calculateTop10FromRedis();
        return ResponseEntity.ok(ApiResponse.success(top10LocationStats));
    }

    @GetMapping("/photo")
    public ResponseEntity<ApiResponse<?>> getPhotoStats(
            @RequestParam("location") String location
    ) {
        PhotosResponseDto photosResponseDto = locationStatService.getLocationPhotos(location);
        return ResponseEntity.ok(ApiResponse.success(photosResponseDto));
    }
}
