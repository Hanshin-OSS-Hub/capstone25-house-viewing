package com.example.houseviewing.service;

import com.example.houseviewing.domain.User;
import com.example.houseviewing.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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
        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getEmail() = " + user.getEmail());
        System.out.println("user.getName() = " + user.getName());
        System.out.println("user.getLoginId() = " + user.getLoginId());
        System.out.println("user.getPassword() = " + user.getPassword());
        Assertions.assertEquals(userId, user.getId());

    }

    @Test
    @DisplayName("이메일과 이름으로 ID 찾기")
    public void findLoginId(){
        User user = getUser();
        String findLoginId = userService.findUserId("yooyoo9191@gmail.com", "yoo");
        System.out.println("findLoginId = " + findLoginId);
        Assertions.assertEquals(user.getLoginId(), findLoginId);
    }

    @Test
    @DisplayName("ID, 이메일, 이름으로 비밀번호 찾기")
    public void findPassword(){
        User user = getUser();
        String userPassword = userService.findUserPassword("1234", "yooyoo9191@gmail.com", "yoo");
        System.out.println("userPassword = " + userPassword);
        Assertions.assertEquals(user.getPassword(), userPassword);
    }


    private User getUser() {
        User user = new User("yooyoo9191@gmail.com", "yoo", "1234", "유인근");
        userRepository.save(user);
        return user;
    }
}