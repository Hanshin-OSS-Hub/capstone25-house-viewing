package com.house.houseviewing.domain.house.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.repository.ContractRepository;
import com.house.houseviewing.domain.house.dto.request.HouseEditRequest;
import com.house.houseviewing.domain.house.dto.response.HouseEditResponse;
import com.house.houseviewing.domain.house.dto.response.HouseMeResponse;
import com.house.houseviewing.domain.house.dto.response.HousesResponse;
import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.registrysnapshot.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshot.repository.RegistrySnapshotRepository;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HouseService {

    private final UserRepository userRepository;
    private final HouseRepository houseRepository;
    private final KakaoAddress kakaoAddress;
    private final ContractRepository contractRepository;
    private final RegistrySnapshotRepository registrySnapshotRepository;

    @Transactional
    public HouseEntity register(Long userId, HouseRegisterRequest request){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        Address address = kakaoAddress.parsingAddress(request.getOriginAddress());

        HouseEntity house = request.toEntity(user, address, MonitoringStatus.OFFLINE);
        HouseEntity savedHouse = houseRepository.save(house);
        if(user.isPremium()){
            savedHouse.updateMonitoringStatus(MonitoringStatus.LIVE);
        }
        user.addHouse(savedHouse);
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

    public List<HousesResponse> getHouses(Long userId){
        List<HouseEntity> houses = houseRepository.findByUserEntityId(userId);

        return houses.stream()
                .map(HousesResponse::from)
                .toList();
    }

    public HouseMeResponse getHouse(Long userId, Long houseId){
        HouseEntity house = houseRepository.findByUserEntityIdAndId(userId, houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));
        ContractEntity contract = contractRepository.findTopByHouseEntityIdOrderByCreatedAtDesc(house.getId())
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        RegistrySnapshotEntity snapshot = registrySnapshotRepository.findTopByHouseEntityIdOrderByCreatedAtDesc(house.getId())
                .orElseThrow(() -> new AppException(ExceptionCode.SNAPSHOT_NOT_FOUND));
        return HouseMeResponse.from(house, contract, snapshot);
    }

    @Transactional
    public HouseEditResponse editHouse(Long userId, Long houseId, HouseEditRequest request){
        HouseEntity house = houseRepository.findByUserEntityIdAndId(userId, houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));
        editRequest(request, house);

        return HouseEditResponse.from(house);
    }

    private void editRequest(HouseEditRequest request, HouseEntity house) {
        if(request.getNickname() != null) {
            house.updateNickname(request.getNickname());
        }
        if(request.getAddress() != null) {
            Address address = kakaoAddress.parsingAddress(request.getAddress());
            house.updateAddress(address);
        }
    }
}
