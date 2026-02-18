package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.enums.MonitoringStatus;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.external.kakao.client.KakaoApiClientImpl;
import com.house.houseviewing.global.external.kakao.service.KakaoAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HouseService {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final KakaoAddressService kakaoAddressService;

    @Transactional
    public Long register(HouseRegisterRQ request){

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));
        Address address = kakaoAddressService.parsingAddress(request.getOriginAddress());
        HouseEntity house = new HouseEntity(request.getNickname(), address, null, MonitoringStatus.OFFLINE);
        houseRepository.save(house);
        user.addHouse(house);

        return house.getId();
    }

    @Transactional
    public void delete(Long houseId){
        HouseEntity saved = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        houseRepository.deleteById(saved.getId());
    }
}
