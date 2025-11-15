package com.Just_112_More.PicPle.stat.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotPlaceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpleMessagingTemplate;
    private final LocationStatService locationStatService;
    private static final String TOP10_KEY = "top10::hotplaces";

    public void saveTop10Cache(HotPlaceResponseList top10LocationStats) {
        try{
            String json = objectMapper.writeValueAsString(top10LocationStats);
            redisTemplate.opsForValue().set(TOP10_KEY, json);
        } catch (Exception e){
            throw new CustomException(ErrorCode.REDIS_SAVE_FAIL);
        }
    }

    public HotPlaceResponseList getTop10Cache(){
        String json = redisTemplate.opsForValue().get(TOP10_KEY);
        if (json == null) return null;
        try{
            return objectMapper.readValue(json, HotPlaceResponseList.class);
        } catch (Exception e){
            throw new CustomException(ErrorCode.REDIS_READ_FAIL);
        }
    }

    public void checkListAndBroadcast(){
        HotPlaceResponseList oldList = getTop10Cache();
        HotPlaceResponseList newList = locationStatService.calculateTop10FromDB();

        if (!Objects.equals(oldList, newList)) {
            saveTop10Cache(newList); // 새로운 내용으로 갱신하여 저장
            simpleMessagingTemplate.convertAndSend("/topic/hot-places", newList);
            log.info("Hotplaces TOP10 변경 감지 → Redis 갱신 및 broadcast 완료");
        }
    }
}