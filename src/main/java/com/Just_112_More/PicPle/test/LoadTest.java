package com.Just_112_More.PicPle.test;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.stat.dto.HotPlaceResponseList;
import com.Just_112_More.PicPle.stat.service.LocationStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoadTest {

    private final LocationStatService locationStatService;

    @GetMapping("/test/query")
    public ResponseEntity<ApiResponse<?>> getPhotolankbyquery(){
        HotPlaceResponseList list = locationStatService.calculateTop10FromDB();
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
