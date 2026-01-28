package com.house.houseviewing.service;

import com.house.houseviewing.api.dto.UserRegisterRequest;
import com.house.houseviewing.domain.User;
import com.house.houseviewing.repository.UserRepository;
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
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("유저 회원등록")
    void 회원가입_성공(){
        //given
        UserRegisterRequest request = new UserRegisterRequest("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        Long userId = userService.save(request);

        //when
        User user = userRepository.findById(userId).get();

        //then
        assertThat(user.getId()).isEqualTo(userId);
        assertThat("유인근").isEqualTo(user.getName());
        assertThat("yooyoo9191@gmail.com").isEqualTo(user.getEmail());
        assertThat("yooyoo9191").isEqualTo(user.getLoginId());
        assertThat("okok0635!").isEqualTo(user.getPassword());

    }

}