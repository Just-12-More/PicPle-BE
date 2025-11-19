package com.Just_112_More.PicPle.photo.service;

import com.Just_112_More.PicPle.photo.domain.PhotoChangedEvent;
import com.Just_112_More.PicPle.stat.service.HotPlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoChangedListener {
    private final HotPlaceService hotPlaceService;

    // DB 커밋후 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onPhotoChanged(PhotoChangedEvent event) {
        log.info("사진변경 이벤트 수신: {}", event.locationLabel());
        hotPlaceService.checkListAndBroadcast();
    }
}
