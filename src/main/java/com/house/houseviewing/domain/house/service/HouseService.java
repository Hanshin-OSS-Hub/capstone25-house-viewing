package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;
import com.house.houseviewing.domain.house.dto.request.HouseRegisterRequest;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.external.kakao.service.KakaoAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HouseService {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final KakaoAddress kakaoAddress;

    @Transactional
    public HouseEntity register(Long userId, HouseRegisterRequest request){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        Address address = kakaoAddress.parsingAddress(request.getOriginAddress());

        HouseEntity house = HouseEntity.builder()
                .nickname(request.getNickname())
                .address(address)
                .monitoringStatus(MonitoringStatus.OFFLINE)
                .build();
        HouseEntity savedHouse = houseRepository.save(house);
        if(user.isPremium()){
            savedHouse.updateMonitoringStatus(MonitoringStatus.LIVE);
        }
        user.addHouse(savedHouse);
        house.addUser(user);
        return savedHouse;
    }

    @Transactional
    public void delete(Long userId, Long houseId){
        HouseEntity saved = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));

        if(!saved.getUserEntity().getId().equals(userId)){
            throw new AppException(ExceptionCode.FORBIDDEN);
        }

        houseRepository.deleteById(saved.getId());
    }
}
