package com.example.houseviewing.service;

import com.example.houseviewing.domain.User;
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

    @Test
    @DisplayName("회원가입")
    public void userSave(){
        User user = getUser();
        System.out.println("user.getId() = " + user.getId());
        System.out.println("user.getEmail() = " + user.getEmail());
        System.out.println("user.getName() = " + user.getName());
        System.out.println("user.getLoginId() = " + user.getLoginId());
        System.out.println("user.getPassword() = " + user.getPassword());
        Assertions.assertNotNull(user.getId());

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

    @Test
    @DisplayName("비밀번호 변경하기")
    public void userChangePassword(){
        User user = getUser();
        String password = userService.updatePassword(user.getPassword(), "okok0635");
        System.out.println("password = " + password);
        org.assertj.core.api.Assertions.assertThat(password).isEqualTo("okok0635");
    }

    private User getUser() {
        User user = new User("yooyoo9191@gmail.com", "yoo", "1234", "유인근");
        userService.saveUser(user);
        return user;
    }
}