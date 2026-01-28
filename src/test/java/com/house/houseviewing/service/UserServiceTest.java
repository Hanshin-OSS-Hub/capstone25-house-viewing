package com.house.houseviewing.service;

import com.house.houseviewing.api.UserApiController;
import com.house.houseviewing.domain.User;
import com.house.houseviewing.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserApiController userApiController;

    @Test
    @DisplayName("유저 회원등록")
    void 회원가입(){
    }

    private static User getUser() {
        return new User("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
    }


}