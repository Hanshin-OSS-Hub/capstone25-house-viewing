package com.house.houseviewing.domain.user.model.findid;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class UserFindIdRQ {

    @Email
    private String email;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;
}
