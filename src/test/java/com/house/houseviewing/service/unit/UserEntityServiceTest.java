package com.house.houseviewing.service.unit;

import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.fixture.UserFixture;
import com.house.houseviewing.domain.user.service.UserService;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class UserEntityServiceTest {

    @InjectMocks UserService userService;
    @Mock UserRepository userRepository;

    @Test
    @DisplayName("아이디 중복 예외 발생")
    void 아이디_중복_예외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        UserRegisterRQ request = UserFixture.createRegister(build).build();

        given(userRepository.existsByLoginId(anyString()))
                .willReturn(true);
        // when
        // then
        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.DUPLICATE_LOGIN_ID);
    }

    @Test
    @DisplayName("이메일 중복 에외 발생")
    void 이메일_중복_예외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        UserRegisterRQ build1 = UserFixture.createRegister(build).build();

        given(userRepository.existsByEmail(anyString()))
                .willReturn(true);
        // when
        // then
        assertThatThrownBy(() -> userService.register(build1))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.DUPLICATE_EMAIL);
    }

    @Test
    @DisplayName("로그인 실패 예외 발생 - 요청 데이터가 없음")
    void 로그인_실패_예외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        UserLoginRQ build1 = UserFixture.createLogin(build).build();
        given(userRepository.findByLoginIdAndPassword(anyString(), anyString()))
                .willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> userService.login(build1))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.LOGIN_FAILED);
    }

    @Test
    @DisplayName("아이디 찾기 실패 예외 발생 - 요청 데이터가 없음")
    void 아이디_찾기_실패_에외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        UserFindIdRQ build1 = UserFixture.createFindId(build).build();
        given(userRepository.findByEmailAndName(anyString(), anyString()))
                .willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> userService.findId(build1))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.FIND_LOGIN_ID_FAILED);
    }

    @Test
    @DisplayName("비밀번호 권한 예외 발생 - 요청 데이터가 없음")
    void 비밀번호_권한_에외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        UserVerifyPasswordRQ build1 = UserFixture.createVerifyPassword(build).build();
        given(userRepository.findByEmailAndNameAndLoginId(anyString(), anyString(), anyString()))
                .willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> userService.passwordVerify(build1))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.VERIFY_PASSWORD_FAILED);
    }

    @Test
    @DisplayName("비밀번호 변경 예외 발생 - 비밀번호 확인과 다름")
    void 비밀번호_변경_예외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        UserResetPasswordRQ build1 = UserFixture.createResetPassword("okok0635!", "okok0635").build();
        // when
        // then
        assertThatThrownBy(() -> userService.passwordReset(build1))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.MISMATCH_PASSWORD);
    }

    @Test
    @DisplayName("유저 삭제 예외 발생 - 유저가 없음")
    void 유저_삭제_예외_발생(){
        // given
        UserEntity build = UserFixture.createDefault().build();
        given(userRepository.findById(build.getId()))
                .willReturn(Optional.empty());
        // when
        // then
        assertThatThrownBy(() -> userService.delete(build.getId()))
                .isInstanceOf(AppException.class)
                .extracting("exceptionCode")
                .isEqualTo(ExceptionCode.USER_NOT_FOUND);
    }

}

