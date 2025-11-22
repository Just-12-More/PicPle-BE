package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.photo.domain.PhotoChangedEvent;
import com.Just_112_More.PicPle.photo.dto.PhotoUpdateEventDto;
import com.Just_112_More.PicPle.stat.service.HotPlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoChangedListener {

    private final HotPlaceService hotPlaceService;
    private final StringRedisTemplate stringRedisTemplate;
    @Value("${urls.s3}")
    private String s3Url;

    // DB 커밋후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPhotoChanged(PhotoUpdateEventDto eventDto) {
        log.info("사진변경 이벤트 수신: {}", eventDto.getLocationLabel());

        // 1) ZSET score 증가 (인기순 정렬 기준)
        stringRedisTemplate.opsForZSet().incrementScore(
                "hotplace:rank", eventDto.getLocationLabel(), 1);

        // 2) HASH 세부정보 업데이트
        Map<String, String> hashData = new HashMap<>();
        hashData.put("locationLabel", eventDto.getLocationLabel());
        hashData.put("latitude", eventDto.getLatitude());
        hashData.put("longitude", eventDto.getLongitude());
        hashData.put("photoCnt", String.valueOf(eventDto.getPhotoCnt()));
        hashData.put("imgUrl", s3Url+eventDto.getImgUrl());

        stringRedisTemplate.opsForHash().putAll(
                "hotplace:hash:" + eventDto.getLocationLabel(),
                hashData
        );

        // 3) TOP10 재계산, broadcast
        hotPlaceService.setListAndBroadcast();
    }
}
