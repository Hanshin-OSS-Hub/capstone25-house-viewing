package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.user.enums.MonitoringStatus;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
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
    public HouseEntity register(HouseRegisterRQ request){

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        Address address = kakaoAddress.parsingAddress(request.getOriginAddress());

        HouseEntity house = new HouseEntity(request.getNickname(), address);
        HouseEntity savedHouse = houseRepository.save(house);
        if(user.checkSubscription()){
            savedHouse.setMonitoringStatus(MonitoringStatus.LIVE);
        }
        user.addHouse(savedHouse);
        house.setUserEntity(user);
        return savedHouse;
    }

    @Transactional
    public void delete(Long houseId){
        HouseEntity saved = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));

        houseRepository.deleteById(saved.getId());
    }
}
