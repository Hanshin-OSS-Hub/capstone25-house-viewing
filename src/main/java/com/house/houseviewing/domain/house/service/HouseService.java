package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.enums.MonitoringStatus;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRS;
import com.house.houseviewing.domain.house.repository.HouseRepository;
import com.house.houseviewing.domain.user.entity.UserEntity;
import com.house.houseviewing.domain.user.repository.UserRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import com.house.houseviewing.global.external.kakao.model.KakaoAddressRS;
import com.house.houseviewing.global.external.kakao.model.KakaoAddressRS.Document;
import com.house.houseviewing.global.external.kakao.model.KakaoAddressRS.ParsedAddress;
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
    public HouseRegisterRS register(HouseRegisterRQ request){

        UserEntity user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));
        KakaoAddressRS response = kakaoAddress.parsingAddress(request.getOriginAddress()).block();
        Document document = response.getDocuments().stream()
                .findFirst()
                .orElseThrow(() -> new AppException(ExceptionCode.ADDRESS_NOT_FOUND));
        ParsedAddress parsedAddress = document.getParsedAddress();
        String detailAddress = extractDetailAddress(request.getOriginAddress());
        Address address = new Address(parsedAddress.getAddressName(), parsedAddress.getRegion1DepthName(),
                parsedAddress.getRegion2DepthName(),parsedAddress.getRegion3DepthName(),
                parsedAddress.getMainAddressNo(),parsedAddress.getSubAddressNo(),detailAddress);
        HouseEntity house = new HouseEntity(request.getNickname(), address, null, MonitoringStatus.OFFLINE);
        houseRepository.save(house);
        user.addHouse(house);

        return new HouseRegisterRS(house.getId(), house.getAddress());
    }

    private String extractDetailAddress(String originAddress) {
        if (!originAddress.contains(",")) {
            return null;
        }
        return originAddress.split(",")[1].trim(); //
    }

    @Transactional
    public void delete(Long houseId){
        HouseEntity saved = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        houseRepository.deleteById(saved.getId());
    }
}
