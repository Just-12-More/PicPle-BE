package com.Just_112_More.PicPle.stat.service;

import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotPlaceInitalizer {
    private final LocationStatService locationStatService;
    private final HotPlaceService hotPlaceService;

    @PostConstruct
    public void initTop10Cache() {
        HotPlaceResponseList top10 = locationStatService.calculateTop10FromDB();
        hotPlaceService.saveTop10Cache(top10);
        log.info("[초기화 작업] Redis에 초기 TOP10 핫플레이스 캐시 저장 완료");
    }
}
