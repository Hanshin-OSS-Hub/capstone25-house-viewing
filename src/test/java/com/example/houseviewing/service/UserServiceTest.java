package com.example.houseviewing.service;

import com.example.houseviewing.domain.User;
import com.example.houseviewing.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.security.PublicKey;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    public void userSave(){
        User user = new User("yooyoo9191@gmail.com", "yoo", "1234", "유인근");
        Long userId = user.getId();

        Assertions.assertEquals(userId, user.getId());

    }

    @Test
    @DisplayName("이메일과 이름으로 ID 찾기")
    public void findByName(){
        User user = new User("yooyoo9191@gmail.com", "yoo", "1234", "유인근");
        userRepository.save(user);
        String findLoginId = userService.findUserId("yooyoo9191@gmail.com", "yoo");
        System.out.println("findLoginId = " + findLoginId);
        Assertions.assertEquals(user.getLoginId(), findLoginId);
    }

}