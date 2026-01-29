package com.house.houseviewing.controller;

import com.house.houseviewing.dto.*;
import com.house.houseviewing.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserRegisterResponse join(@Valid @RequestBody UserRegisterRequest request){
        Long userId = userService.register(request);
        return new UserRegisterResponse(userId);
    }

    @PostMapping("/login")
    public UserLoginResponse login(@Valid @RequestBody UserLoginRequest request){
        Long userId = userService.login(request);
        return new UserLoginResponse(userId);
    }

}
