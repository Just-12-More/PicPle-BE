package com.Just_112_More.PicPle.stat.service;

import com.Just_112_More.PicPle.stat.domain.LocationStat;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotPlaceInitalizer {
    private final LocationStatService locationStatService;
    private final HotPlaceService hotPlaceService;
    private final StringRedisTemplate stringRedisTemplate;

    /*
    @PostConstruct
    public void initTop10Cache() {
        HotPlaceResponseList top10 = locationStatService.calculateTop10FromDB();
        hotPlaceService.saveTop10Cache(top10);
        log.info("[초기화 작업] Redis에 초기 TOP10 핫플레이스 캐시 저장 완료");
    }
     */

    @PostConstruct
    public void initHotPlace(){
        List<LocationStat> allStats = locationStatService.findAll(); // 전체 조회

        for(LocationStat locationStat : allStats){
            String label = locationStat.getLocationLabel();
            int count = locationStat.getPhotoCnt();

            // ZSET 업데이트
            stringRedisTemplate.opsForZSet()
                    .add("hotplace:rank", label, count);
            log.info("[초기화 작업] Redis에 LocationStat {}개 로딩 완료", allStats.size());

            // HASH 업데이트
            Map<String, String> hashData = new HashMap<>();
            hashData.put("locationLabel", label);
            hashData.put("latitude", locationStat.getLatitude());
            hashData.put("longitude", locationStat.getLongitude());
            hashData.put("imgUrl", locationStat.getRepresentativePhotoUrl());
            hashData.put("photoCnt", String.valueOf(locationStat.getPhotoCnt()));

            stringRedisTemplate.opsForHash().putAll(
                    "hotplace:hash:" + locationStat.getLocationLabel(),
                    hashData
            );
            log.info("[초기화 작업] RedisHash초기 top10 핫플레이스 캐시 저장 완료");

        }
    }
}
