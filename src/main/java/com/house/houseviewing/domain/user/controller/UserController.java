package com.house.houseviewing.domain.user.controller;

import com.house.houseviewing.domain.user.dto.response.UserMeResponse;
import com.house.houseviewing.domain.user.dto.request.UserFindIdRequest;
import com.house.houseviewing.domain.user.dto.response.UserFindIdResponse;
import com.house.houseviewing.global.security.CustomUserDetails;
import com.house.houseviewing.domain.common.auth.dto.UserLoginRequest;
import com.house.houseviewing.domain.common.auth.dto.UserLoginResponse;
import com.house.houseviewing.domain.user.dto.request.UserResetPasswordRequest;
import com.house.houseviewing.domain.user.dto.request.UserVerifyPasswordRequest;
import com.house.houseviewing.domain.user.dto.request.UserRegisterRequest;
import com.house.houseviewing.domain.user.dto.response.UserRegisterResponse;
import com.house.houseviewing.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request){
        UserRegisterResponse result = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request){
        UserLoginResponse result = userService.login(request);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/me")
    public ResponseEntity<UserMeResponse> me(@AuthenticationPrincipal CustomUserDetails userDetails){
        UserMeResponse result = userService.me(userDetails.getUserId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/find-id")
    public ResponseEntity<UserFindIdResponse> findLoginId(@Valid @RequestBody UserFindIdRequest request){
        UserFindIdResponse result = userService.findLoginId(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<Boolean> verifyPassword(@Valid @RequestBody UserVerifyPasswordRequest request){
        boolean verify = userService.passwordVerify(request);
        return ResponseEntity.ok().body(verify);
    }

    @PatchMapping("/password/reset")
    public ResponseEntity<Boolean> resetPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UserResetPasswordRequest request){
        boolean b = userService.passwordReset(userDetails.getUserId(), request);
        return ResponseEntity.ok().body(b);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.delete(userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }
}
