package com.Just_112_More.PicPle.exception;

import com.Just_112_More.PicPle.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException customException){
        return ResponseEntity
                .status(customException.getErrorCode().getStatus())
                .body(ApiResponse.fail(null, customException.getErrorCode().name(),
                        customException.getErrorCode().getMessage() ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleOther(Exception exception){
        log.error("[전역 예외] 처리되지 않은 예외 발생", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(null, "INTERNAL_SERVER_ERROR", "서버 내부 오류"));
    }

}
