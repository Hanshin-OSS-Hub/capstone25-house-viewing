package com.house.houseviewing.domain.house.model.register;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class HouseRegisterRQ {

    private Long userId; // 추후 세션이나 JWT로 발급한 토큰에서 사용자 정보를 가져옴

    @NotBlank(message = "주택 이름은 필수입니다.")
    private String nickname;

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String originAddress;

}
