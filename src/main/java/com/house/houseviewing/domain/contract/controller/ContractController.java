package com.house.houseviewing.domain.contract.controller;

import com.house.houseviewing.domain.contract.entity.ContractEntity;
import com.house.houseviewing.domain.contract.model.ContractRegisterRQ;
import com.house.houseviewing.domain.contract.model.ContractRegisterRS;
import com.house.houseviewing.domain.contract.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;

    @PostMapping("/register")
    public ResponseEntity<ContractRegisterRS> join(@Valid @RequestBody ContractRegisterRQ request){
        ContractEntity contract = contractService.register(request);
        ContractRegisterRS register = new ContractRegisterRS(contract.getHouseEntity().getId(), contract.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(register);
    }
}
