package com.house.houseviewing.domain.auth.controller;

import com.house.houseviewing.domain.auth.dto.UserLoginRequest;
import com.house.houseviewing.domain.auth.dto.UserLoginResponse;
import com.house.houseviewing.domain.user.service.UserService;
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

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginResponse result = userService.login(request);
        return ResponseEntity.ok(result);
    }
}
