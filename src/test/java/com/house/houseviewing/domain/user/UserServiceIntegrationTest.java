package com.house.houseviewing.domain.user;

import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.dto.request.UserFindIdRequest;
import com.house.houseviewing.domain.common.auth.dto.UserLoginRQ;
import com.house.houseviewing.domain.user.dto.request.UserResetPasswordRequest;
import com.house.houseviewing.domain.user.dto.request.UserVerifyPasswordRequest;
import com.house.houseviewing.domain.user.dto.request.UserRegisterRequest;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("회원가입")
    void 회원가입(){
        // given
        UserEntity user = UserFixture.createDefault().build();
        UserRegisterRequest request = UserFixture.createRegister(user).build();
        // when
        UserEntity register = userService.register(request);
        // then
        assertThat(register.getId()).isNotNull();
        assertThat(user.getName()).isEqualTo(register.getName());
    }

    @Test
    @DisplayName("로그인")
    void 로그인(){
        // given
        UserEntity register = getUserEntity();
        UserLoginRQ loginRQ = UserLoginRQ.builder()
                .loginId(register.getLoginId())
                .password(register.getPassword())
                .build();
        // when
        Long login = userService.login(loginRQ);
        // then
        assertThat(login).isEqualTo(register.getId());
    }

    @Test
    @DisplayName("아이디 찾기")
    void 아이디_찾기(){
        // given
        UserEntity register = getUserEntity();
        UserFindIdRequest findIdRQ = UserFindIdRequest.builder()
                .email(register.getEmail())
                .name(register.getName())
                .build();
        // when
        String id = userService.findId(findIdRQ);
        // then
        assertThat(register.getLoginId()).isEqualTo(id);
    }

    @Test
    @DisplayName("비밀번호 권한 얻기")
    void 비밀번호_권한_얻기(){
        // given
        UserEntity register = getUserEntity();
        UserVerifyPasswordRequest request = UserVerifyPasswordRequest.builder()
                .email(register.getEmail())
                .loginId(register.getLoginId())
                .name(register.getName())
                .build();
        // when
        boolean b = userService.passwordVerify(request);
        // then
        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("비밀번호 재설정")
    void 비밀번호_재설정(){
        // given
        UserEntity register = getUserEntity();
        UserResetPasswordRequest request = UserResetPasswordRequest.builder()
                .userId(register.getId())
                .newPassword("okok0635")
                .confirmPassword("okok0635")
                .build();
        // when
        boolean b = userService.passwordReset(request);
        // then
        assertThat(b).isTrue();
    }

    @Test
    @DisplayName("틸퇴")
    void 탈퇴(){
        // given
        UserEntity register = getUserEntity();
        // when
        userService.delete(register.getId());
        // then
        assertThat(userRepository.findById(register.getId())).isEmpty();
    }

    private UserEntity getUserEntity() {
        UserEntity build = UserFixture.createDefault().build();
        UserRegisterRequest build1 = UserFixture.createRegister(build).build();
        UserEntity register = userService.register(build1);
        return register;
    }
}
