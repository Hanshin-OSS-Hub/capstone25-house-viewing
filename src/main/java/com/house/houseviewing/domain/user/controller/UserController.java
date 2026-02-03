package com.house.houseviewing.domain.user.controller;

import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRS;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRS;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRS;
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

    @PostMapping("/find/id")
    public UserFindIdRS findId(@Valid @RequestBody UserFindIdRQ request){

        String loginId = userService.findId(request);
        return new UserFindIdRS(loginId);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<Void> verifyPassword(@Valid @RequestBody UserVerifyPasswordRQ request){
        userService.passwordVerify(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody UserResetPasswordRQ request){
        userService.passwordReset(request);
        return ResponseEntity.ok().build();
    }

}
