package com.Just_112_More.PicPle.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // auth error
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근권한이 없습니다."),
    NOT_LOGIN_USER(HttpStatus.FORBIDDEN, "로그인하지 않은 사용자입니다."),
    INVALID_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_CORRECT_ERROR(HttpStatus.UNAUTHORIZED, "토큰이 올바르지 않습니다."),
    TOKEN_GENERATION_ERROR(HttpStatus.UNAUTHORIZED, "토큰생성에 실패하였습니다.");

    private final HttpStatus status;
    private final String message;
}
