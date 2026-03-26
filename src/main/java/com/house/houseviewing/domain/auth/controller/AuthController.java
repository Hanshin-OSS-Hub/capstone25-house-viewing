package com.house.houseviewing.domain.auth.controller;

import com.house.houseviewing.domain.auth.dto.request.UserLoginRequest;
import com.house.houseviewing.domain.auth.dto.response.UserLoginResponse;
import com.house.houseviewing.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginResponse result = authService.login(request);
        return ResponseEntity.ok(result);
    }
}
