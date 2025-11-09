package com.Just_112_More.PicPle.test;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/test-hot")
    public String sendTestHotPlaces(){
        // 더미 데이터
        List<String> hotPlaces = List.of("건대", "상봉", "음성");

        // /topic/hot-places 를 구독중인 모든 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/hot-places", hotPlaces);

        return "HotPlaces broadcasted!";
    }

}
