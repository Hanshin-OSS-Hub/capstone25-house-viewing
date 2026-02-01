package com.house.houseviewing.domain.user.model.password.reset;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserResetPasswordRQ {

    private Long userId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String newPassword;

    @NotBlank(message = "비밀번호가 틀렸습니다.")
    private String confirmPassword;

}
