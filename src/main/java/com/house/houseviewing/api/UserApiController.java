package com.house.houseviewing.api;

import com.house.houseviewing.api.dto.UserRegisterRequest;
import com.house.houseviewing.api.dto.UserRegisterResponse;
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
public class UserApiController {

    private final UserService userService;

    @PostMapping
    public UserRegisterResponse join(@Valid @RequestBody UserRegisterRequest request){
        Long userId = userService.save(request);
        return new UserRegisterResponse(userId);
    }
}
