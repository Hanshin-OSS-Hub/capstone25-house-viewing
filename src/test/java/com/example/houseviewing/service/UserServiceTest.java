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
        Long savedId = userService.saveUser(user);
        Assertions.assertEquals(user.getId(),savedId);

    }

    @Test
    @DisplayName("이름으로 User 찾기")
    public void findByName(){
        User user = new User("yooyoo9191@gmail.com", "yoo", "1234", "유인근");
        userService.saveUser(user);
        List<User> name = userRepository.findByName(user.getName());
        Assertions.assertEquals("유인근", name.get(0).getName(), "조회된 이름이 일치해야한다");
    }

}