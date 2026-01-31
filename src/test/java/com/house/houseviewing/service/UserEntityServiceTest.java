package com.house.houseviewing.service;

import com.house.houseviewing.domain.global.exception.AppException;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.global.jpa.entity.UserEntity;
import com.house.houseviewing.domain.global.jpa.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class UserEntityServiceTest {

    @Autowired
    UserService userService;
    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    void 회원가입_성공(){
        //given
        UserRegisterRQ userDto = getUserDto();
        Long userId = userService.register(userDto);

        //when
        UserEntity userEntity = userRepository.findById(userId).get();

        //then
        assertThat(userEntity.getId()).isEqualTo(userId);
        assertThat("유인근").isEqualTo(userEntity.getName());
        assertThat("yooyoo9191@gmail.com").isEqualTo(userEntity.getEmail());
        assertThat("yooyoo9191").isEqualTo(userEntity.getLoginId());
        assertThat("okok0635!").isEqualTo(userEntity.getPassword());
    }


    @Test
    @DisplayName("ID 중복확인")
    void 아이디_중복_예외발생(){
        //given
        UserRegisterRQ userDto = getUserDto();
        Long savedId = userService.register(userDto);

        //when

        //then
        Assertions.assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(AppException.class);
    }

//    @Test
//    @DisplayName("로그인")
//    void 로그인_성공(){
//        // given
//        UserRegisterRQ userDto = getUserDto();
//        Long savedId = userService.register(userDto);
//
//        // when
//        UserLoginRQ user = new UserLoginRQ("yooyoo9191", "okok0635!");
//        Long login = userService.login(user);
//
//        // then
//        assertThat(login).isEqualTo(savedId);
//    }
//
//    @Test
//    @DisplayName("로그인 실패")
//    void 로그인_실패(){
//        UserRegisterRQ userDto = getUserDto();
//        Long savedId = userService.register(userDto);
//
//        // when
//        UserLoginRQ user = new UserLoginRQ("yooyoo9191", "okok0635");
//
//        // then
//        assertThatThrownBy(() -> userService.login(user))
//                .isInstanceOf(AppException.class);
//
//    }


    private UserRegisterRQ getUserDto() {
        UserRegisterRQ request = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        return request;
    }

}