package com.Just_112_More.PicPle.user.controller;

import com.Just_112_More.PicPle.user.dto.LoginRequest;
import com.Just_112_More.PicPle.user.dto.LoginResponse;
import com.Just_112_More.PicPle.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginDto) {
        String jwt = authService.loginViaOAuth(loginDto);
        return ResponseEntity.ok(new LoginResponse(jwt));
    }
}
