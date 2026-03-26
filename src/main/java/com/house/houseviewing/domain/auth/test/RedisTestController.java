package com.house.houseviewing.domain.auth.test;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisTestController {

    private final RedisTestService redisTestService;

    @GetMapping("/test/save")
    public String save(){
        redisTestService.saveTestValue();
        return "saved";
    }

    @GetMapping("/test/get")
    public String get(){
        return redisTestService.getTestValue();
    }
}
