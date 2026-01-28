package com.house.houseviewing.service;

import com.house.houseviewing.dto.UserLoginRequest;
import com.house.houseviewing.dto.UserRegisterRequest;
import com.house.houseviewing.domain.User;
import com.house.houseviewing.exception.DuplicateLoginIdException;
import com.house.houseviewing.exception.LoginFailedException;
import com.house.houseviewing.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    void 회원가입_성공(){
        //given
        UserRegisterRequest userDto = getUserDto();
        Long userId = userService.register(userDto);

        //when
        User user = userRepository.findById(userId).get();

        //then
        assertThat(user.getId()).isEqualTo(userId);
        assertThat("유인근").isEqualTo(user.getName());
        assertThat("yooyoo9191@gmail.com").isEqualTo(user.getEmail());
        assertThat("yooyoo9191").isEqualTo(user.getLoginId());
        assertThat("okok0635!").isEqualTo(user.getPassword());
    }


    @Test
    @DisplayName("ID 중복확인")
    void 아이디_중복_예외발생(){
        //given
        UserRegisterRequest userDto = getUserDto();
        Long savedId = userService.register(userDto);

        //when

        //then
        Assertions.assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(DuplicateLoginIdException.class);
    }

    @Test
    @DisplayName("로그인")
    void 로그인_성공(){
        // given
        UserRegisterRequest userDto = getUserDto();
        Long savedId = userService.register(userDto);

        // when
        UserLoginRequest user = new UserLoginRequest("yooyoo9191", "okok0635!");
        Long login = userService.login(user);

        // then
        assertThat(login).isEqualTo(savedId);
    }

    @Test
    @DisplayName("로그인 실패")
    void 로그인_실패(){
        UserRegisterRequest userDto = getUserDto();
        Long savedId = userService.register(userDto);

        // when
        UserLoginRequest user = new UserLoginRequest("yooyoo9191", "okok0635");

        // then
        assertThatThrownBy(() -> userService.login(user))
                .isInstanceOf(LoginFailedException.class);

    }


    private UserRegisterRequest getUserDto() {
        UserRegisterRequest request = new UserRegisterRequest("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        return request;
    }

}