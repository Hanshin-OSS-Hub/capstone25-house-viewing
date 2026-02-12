package com.house.houseviewing.domain.contract.controller;

import com.house.houseviewing.domain.contract.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contracts")
public class ContractController {
    private final ContractService contractService;

}
