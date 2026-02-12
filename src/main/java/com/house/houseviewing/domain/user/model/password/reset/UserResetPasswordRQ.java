package com.house.houseviewing.domain.user.model.password.reset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserResetPasswordRQ {

    private Long userId;

    @Size(min = 8, message = "비밀번호를 8자 이상 입력해주세요")
    private String newPassword;

    @NotBlank(message = "비밀번호가 일치하지 않습니다.")
    private String confirmPassword;

}
