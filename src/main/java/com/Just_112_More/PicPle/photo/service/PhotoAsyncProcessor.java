package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
import com.Just_112_More.PicPle.stat.service.LocationStatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoAsyncProcessor {

    private final PhotoService photoService;
    private final PhotoRepository photoRepository;
    private final LocationStatService locationStatService;

    @Async("photoWorkerExecutor")
    public void processPhotoAsync(Long photoId, double lat, double lon, String photoUrl) {
        try {
            // 1) 리버스 지오코딩 - locationlabel, roadaddress추출
            List<String> addressList = photoService.reverseGeoCoding(lat, lon);
            String roadAddress = addressList.get(0);
            String locationLabel = addressList.get(1);

            // 2) photo 업데이트
            photoService.updatePhotoAddress(photoId, roadAddress, locationLabel);

            // 3) LocationStat 업데이트
            locationStatService.uploadStat(locationLabel, roadAddress, photoUrl);
        } catch (Exception e) {
            log.error("Async photo processing failed. photoId=" + photoId, e);
        }

    }

}
