package com.example.houseviewing.service;

import com.example.houseviewing.domain.User;
import com.example.houseviewing.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void userSave(){
        User user = new User("yoo", "1234", "유인근");
        Long savedId = userService.saveUser(user);
        Assertions.assertEquals(user.getId(),savedId);
    }

}