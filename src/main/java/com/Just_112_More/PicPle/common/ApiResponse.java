package com.Just_112_More.PicPle.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;

@JsonPropertyOrder({"isSuccess", "data", "error"})
public class ApiResponse <T>{
    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private T data;
    private ApiError error; // 성공시 null

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public T getData() {
        return data;
    }

    public ApiError getError() {
        return error;
    }

    @Builder
    private ApiResponse(String path, T data, Boolean isSuccess, ApiError error ){
        this.isSuccess = isSuccess;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data){
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
