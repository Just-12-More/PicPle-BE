package com.Just_112_More.PicPle.common;

import lombok.Builder;

public class ApiResponse <T>{
    private boolean isSuccess;
    private T data;
    private ApiError error; // 성공시 null

    @Builder
    private ApiResponse(String path, T data, Boolean isSuccess, ApiError error ){
        this.isSuccess = isSuccess;
        this.data = data;
        this.error = error;
    }

    private static <T> ApiResponse<T> success(T data){
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> fail(T data, String code, String message){
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .data(data)
                .error(new ApiError(code, message))
                .build();
    }
}
