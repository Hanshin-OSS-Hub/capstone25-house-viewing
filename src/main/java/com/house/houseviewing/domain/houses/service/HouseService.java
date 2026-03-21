package com.house.houseviewing.domain.houses.service;

import com.house.houseviewing.domain.common.Address;
import com.house.houseviewing.domain.contracts.entity.ContractEntity;
import com.house.houseviewing.domain.contracts.repository.ContractRepository;
import com.house.houseviewing.domain.houses.dto.request.HouseEditRequest;
import com.house.houseviewing.domain.houses.dto.response.HouseEditResponse;
import com.house.houseviewing.domain.houses.dto.response.HouseMeResponse;
import com.house.houseviewing.domain.houses.dto.response.HousesResponse;
import com.house.houseviewing.domain.houses.entity.HouseEntity;
import com.house.houseviewing.domain.postcontractanalyses.entity.RegistryAnalysisEntity;
import com.house.houseviewing.domain.postcontractanalyses.repository.RegistryAnalysisRepository;
import com.house.houseviewing.domain.registrysnapshots.entity.RegistrySnapshotEntity;
import com.house.houseviewing.domain.registrysnapshots.repository.RegistrySnapshotRepository;
import com.house.houseviewing.domain.users.enums.MonitoringStatus;
import com.house.houseviewing.domain.houses.dto.request.HouseRegisterRequest;
import com.house.houseviewing.domain.houses.repository.HouseRepository;
import com.house.houseviewing.domain.users.entity.UserEntity;
import com.house.houseviewing.domain.users.repository.UserRepository;
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
    private final RegistryAnalysisRepository registryAnalysisRepository;

    @Transactional
    public HouseEntity register(Long userId, HouseRegisterRequest request){

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ExceptionCode.USER_NOT_FOUND));

        Address address = kakaoAddress.parsingAddress(request.getOriginAddress());

        HouseEntity house = request.toEntity(address, MonitoringStatus.OFFLINE);
        if(user.isPremium()){
            house.updateMonitoringStatus(MonitoringStatus.LIVE);
        }
        user.addHouse(house);

        return houseRepository.save(house);
    }

    @Transactional
    public void delete(Long userId, Long houseId){
        HouseEntity saved = houseRepository.findById(houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));

        if(!saved.getUser().getId().equals(userId)){
            throw new AppException(ExceptionCode.FORBIDDEN);
        }

        houseRepository.deleteById(saved.getId());
    }

    public List<HousesResponse> getHouses(Long userId){
        List<HouseEntity> houses = houseRepository.findByUserId(userId);

        return houses.stream()
                .map(HousesResponse::from)
                .toList();
    }

    public HouseMeResponse getHouse(Long userId, Long houseId){
        HouseEntity house = houseRepository.findByUserIdAndId(userId, houseId)
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));
        ContractEntity contract = contractRepository.findTopByHouseIdOrderByCreatedAtDesc(house.getId())
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        RegistrySnapshotEntity snapshot = registrySnapshotRepository.findTopByHouseIdOrderByCreatedAtDesc(house.getId())
                .orElseThrow(() -> new AppException(ExceptionCode.SNAPSHOT_NOT_FOUND));
        RegistryAnalysisEntity analysis = registryAnalysisRepository.findTopBySnapshotIdOrderByCreatedAtDesc(snapshot.getId())
                .orElseThrow(() -> new AppException(ExceptionCode.ANALYSIS_NOT_FOUND));
        return HouseMeResponse.from(contract, analysis);
    }

    @Transactional
    public HouseEditResponse editHouse(Long userId, Long houseId, HouseEditRequest request){
        HouseEntity house = houseRepository.findByUserIdAndId(userId, houseId)
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
