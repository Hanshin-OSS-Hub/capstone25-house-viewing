package com.house.houseviewing.domain.house.controller;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRS;
import com.house.houseviewing.domain.house.service.HouseService;
import com.house.houseviewing.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/houses")
public class HouseController {

    private final HouseService houseService;

    @PostMapping("/register")
    public ResponseEntity<HouseRegisterRS> register(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody HouseRegisterRQ request){
        HouseEntity house = houseService.register(userDetails.getUserId(), request);
        HouseRegisterRS result = HouseRegisterRS.builder()
                .houseId(house.getId())
                .address(house.getAddress())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{houseId}")
    public ResponseEntity<Void> deleteHouse(
            @PathVariable Long houseId,
            @AuthenticationPrincipal CustomUserDetails userDetails){
        houseService.delete(userDetails.getUserId(),houseId);
        return ResponseEntity.noContent().build();
    }
}
