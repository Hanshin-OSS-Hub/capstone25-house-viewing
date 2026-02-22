package com.house.houseviewing.fixture;

import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.model.register.UserRegisterRQ;

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
}
