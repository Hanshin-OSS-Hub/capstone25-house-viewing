package com.house.houseviewing.domain.houses.dto.request;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.houses.entity.HouseEntity;
import com.house.houseviewing.domain.users.enums.MonitoringStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Builder
public class HouseRegisterRequest {

    @NotBlank(message = "주택 이름은 필수입니다.")
    private String nickname;

    @NotBlank(message = "주소 입력은 필수입니다.")
    private String originAddress;

    public HouseEntity toEntity(Address address, MonitoringStatus monitoringStatus){
        return HouseEntity.builder()
                .nickname(nickname)
                .address(address)
                .monitoringStatus(monitoringStatus)
                .build();
    }
}
