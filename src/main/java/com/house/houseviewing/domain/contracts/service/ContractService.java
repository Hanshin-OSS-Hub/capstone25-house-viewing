package com.house.houseviewing.domain.contracts.service;

import com.house.houseviewing.domain.contracts.entity.ContractEntity;
import com.house.houseviewing.domain.contracts.dto.request.ContractRegisterRequest;
import com.house.houseviewing.domain.contracts.repository.ContractRepository;
import com.house.houseviewing.domain.houses.entity.HouseEntity;
import com.house.houseviewing.domain.houses.repository.HouseRepository;
import com.house.houseviewing.global.exception.AppException;
import com.house.houseviewing.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContractService {

    private final HouseRepository houseRepository;
    private final ContractRepository contractRepository;

    @Transactional
    public ContractEntity register(ContractRegisterRequest request){
        HouseEntity house = houseRepository.findById(request.getHouseId())
                .orElseThrow(() -> new AppException(ExceptionCode.HOUSE_NOT_FOUND));
        ContractEntity contract = request.toEntity();
        house.addContract(contract);
        return contractRepository.save(contract);
    }

    @Transactional
    public void delete(Long contractId){
        ContractEntity byId = contractRepository.findById(contractId)
                .orElseThrow(() -> new AppException(ExceptionCode.CONTRACT_NOT_FOUND));
        contractRepository.delete(byId);
    }
}
