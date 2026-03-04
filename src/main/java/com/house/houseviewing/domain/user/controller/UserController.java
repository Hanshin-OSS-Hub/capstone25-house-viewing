package com.house.houseviewing.domain.user.controller;

import com.house.houseviewing.domain.user.entity.UserEntity;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegisterRS> join(@Valid @RequestBody UserRegisterRQ request){
        UserEntity register = userService.register(request);
        UserRegisterRS result = new UserRegisterRS(register.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginRS> login(@Valid @RequestBody UserLoginRQ request){
        Long userId = userService.login(request);
        UserLoginRS result = new UserLoginRS(userId);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/find-id")
    public ResponseEntity<UserFindIdRS> findLoginId(@Valid @ModelAttribute UserFindIdRQ request){
        String loginId = userService.findLoginId(request);
        UserFindIdRS result = new UserFindIdRS(loginId);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/password/verify")
    public ResponseEntity<Boolean> verifyPassword(@Valid @RequestBody UserVerifyPasswordRQ request){
        boolean verify = userService.passwordVerify(request);
        return ResponseEntity.ok().body(verify);
    }

    @PatchMapping("/password/reset")
    public ResponseEntity<Boolean> resetPassword(@Valid @RequestBody UserResetPasswordRQ request){
        boolean b = userService.passwordReset(request);
        return ResponseEntity.ok().body(b);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId){
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}
