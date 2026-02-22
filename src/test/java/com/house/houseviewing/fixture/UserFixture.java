package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.domain.user.model.login.UserLoginRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import com.house.houseviewing.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserFixture {

    public static UserEntity.UserEntityBuilder createDefault(){
        return UserEntity.builder()
                .name("유인근")
                .loginId("yooyoo9191")
                .password("okok0635!")
                .email("yooyoo9191@gmail.com");
    }

    public static UserRegisterRQ.UserRegisterRQBuilder createRegister(UserEntity user){
        return UserRegisterRQ.builder()
                .name(user.getName())
                .email(user.getEmail())
                .loginId(user.getLoginId())
                .password(user.getPassword());
    }

    public static UserLoginRQ.UserLoginRQBuilder createLogin(UserEntity user){
        return UserLoginRQ.builder()
                .loginId(user.getLoginId())
                .password(user.getPassword());
    }

    public static UserFindIdRQ.UserFindIdRQBuilder createFindId(UserEntity user){
        return UserFindIdRQ.builder()
                .name(user.getName())
                .email(user.getEmail());
    }

    public static UserVerifyPasswordRQ.UserVerifyPasswordRQBuilder createVerifyPassword(UserEntity user){
        return UserVerifyPasswordRQ.builder()
                .name(user.getName())
                .loginId(user.getLoginId())
                .email(user.getEmail());
    }

    public static UserResetPasswordRQ.UserResetPasswordRQBuilder createResetPassword(UserEntity user, String newPassword, String confirmPassword){
        return UserResetPasswordRQ.builder()
                .userId(user.getId())
                .newPassword(newPassword)
                .confirmPassword(confirmPassword);
    }
}
