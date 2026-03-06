package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.subscription.entity.SubscriptionEntity;
import com.house.houseviewing.domain.subscription.enums.PlanType;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.findid.UserFindIdRQ;
import com.house.houseviewing.global.security.model.UserLoginRQ;
import com.house.houseviewing.domain.user.model.password.reset.UserResetPasswordRQ;
import com.house.houseviewing.domain.user.model.password.verify.UserVerifyPasswordRQ;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserFixture {

    public static UserEntity.UserEntityBuilder createDefault(){

        SubscriptionEntity subscription = SubscriptionEntity.builder()
                .planType(PlanType.FREE)
                .build();

        return UserEntity.builder()
                .name("유인근")
                .loginId("yooyoo9191")
                .password("okok0635!")
                .email("yooyoo9191@gmail.com")
                .subscription(subscription);
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
