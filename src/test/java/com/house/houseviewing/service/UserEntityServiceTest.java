package com.house.houseviewing.service;

import com.house.houseviewing.domain.global.exception.AppException;
import com.house.houseviewing.domain.global.exception.ExceptionCode;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        UserRegisterRQ request = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        Long userId = userService.register(request);

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
        UserRegisterRQ userDto = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        Long savedId = userService.register(userDto);

        //when

        //then
        assertThatThrownBy(() -> userService.register(userDto))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("로그인")
    void 로그인_성공(){
        // given
        UserEntity user1 = user();

        // when
        UserLoginRQ user = new UserLoginRQ("yooyoo9191", "okok0635!");
        Long login = userService.login(user);

        // then
        assertThat(login).isEqualTo(user1.getId());
    }

    @Test
    @DisplayName("로그인 실패")
    void 로그인_실패(){
        UserEntity user1 = user();

        // when
        UserLoginRQ user = new UserLoginRQ("yooyoo9191", "okok0635");

        // then
        assertThatThrownBy(() -> userService.login(user))
                .isInstanceOf(AppException.class)
                .satisfies(e -> {
                    AppException ex = (AppException) e;
                    assertThat(ex.getExceptionCode())
                            .isEqualTo(ExceptionCode.LOGIN_FAILED);
                });
    }

    @Test
    @DisplayName("아이디 찾기")
    void 아이디_찾기(){

        UserEntity user1 = user();

        UserFindIdRQ request = new UserFindIdRQ("yooyoo9191@gmail.com", "유인근");
        String loginId = userService.findId(request);

        assertThat(user1.getLoginId()).isEqualTo(loginId);
    }

    @Test
    @DisplayName("비밀번호 찾기(사용자 검증)")
    void 비밀번호_찾기_사용자_검증(){

        UserEntity user1 = user();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void 비밀번호_변경(){
        UserEntity user = user();
        userService.passwordReset(new UserResetPasswordRQ(user.getId(), "12345678", "12345678"));

        assertThat(user.getPassword()).isEqualTo("12345678");
    }

    private UserEntity user() {
        UserRegisterRQ request = new UserRegisterRQ("유인근", "yooyoo9191@gmail.com", "yooyoo9191", "okok0635!");
        Long id = userService.register(request);
        UserEntity user = userRepository.findById(id).get();
        return user;
    }
}