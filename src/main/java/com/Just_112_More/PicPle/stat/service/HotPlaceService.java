package com.Just_112_More.PicPle.stat.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class HotPlaceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;
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


}