package com.house.houseviewing.domain.user.controller;

import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRS;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRS;
import com.house.houseviewing.domain.user.service.UserService;
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
    public UserRegisterRS join(@Valid @RequestBody UserRegisterRQ request){
        Long userId = userService.register(request);
        return new UserRegisterRS(userId);
    }

    @PostMapping("/login")
    public UserLoginRS login(@Valid @RequestBody UserLoginRQ request){
        Long userId = userService.login(request);
        return new UserLoginRS(userId);
    }

}
