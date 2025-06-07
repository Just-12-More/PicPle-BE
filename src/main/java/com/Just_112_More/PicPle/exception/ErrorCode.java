package com.Just_112_More.PicPle.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // spring security exception
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근권한이 없습니다."),
    NOT_LOGIN_USER(HttpStatus.FORBIDDEN, "로그인하지 않은 사용자입니다."),

    // user exception
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유저입니다."),
    USER_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 탈퇴처리된 유저입니다." ),
    USER_NOT_FOUND_OR_DELETED(HttpStatus.NOT_FOUND, "존재하지 않은 유저 혹은 탈퇴처리된 유저입니다."),
    USER_IMAGE_GET_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "유저 프로필 이미지 다운로드에 실패했습니다."),
    USER_NAME_GET_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "유저 이름 로드에 실패했습니다." ),
    USER_IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "유저 프로필 업로드에 실패했습니다." ),
    USER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "유저정보를 찾을 수 없습니다." ),

    // jwt 내부 에러
    INVALID_TOKEN_SIGNATURE(HttpStatus.FORBIDDEN, "JWT 서명 검증 실패"),
    MALFORMED_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "잘못된 JWT 형식입니다."),
    UNSUPPORTED_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT입니다."),
    EMPTY_TOKEN_ERROR(HttpStatus.BAD_REQUEST, "토큰이 비어있습니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_PARSING_FAILED(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰 - 파싱실패"),

    // jwt 인증에러
    ACCESS_TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "Access Token이 요청에 포함되지 않았습니다."),
//    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
//    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Access Token입니다."),
    MISMATCHED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "저장된 리프레시 토큰과 일치하지 않습니다."),

    // oauth login 관련 에러
    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 SDK code입니다."),
    OAUTH_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "OAuth 인증 서버에 연결할 수 없습니다."),
    OAUTH_RESPONSE_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "OAuth 사용자 정보 응답이 잘못되었습니다."),
    REDIS_SAVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "리프레시 토큰 저장에 실패했습니다.");

    private final HttpStatus status;
    private final String message;
}
