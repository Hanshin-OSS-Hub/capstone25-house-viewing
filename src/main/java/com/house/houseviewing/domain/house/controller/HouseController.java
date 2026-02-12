package com.house.houseviewing.domain.house.controller;

import com.house.houseviewing.domain.house.entity.HouseEntity;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRQ;
import com.house.houseviewing.domain.house.model.register.HouseRegisterRS;
import com.house.houseviewing.domain.house.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/houses")
public class HouseController {

    private final HouseService houseService;

    @PostMapping("/register")
    public ResponseEntity<HouseRegisterRS> join(@Valid @RequestBody HouseRegisterRQ request){
        Long saved = houseService.register(request);
        HouseRegisterRS result = new HouseRegisterRS(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/delete/{houseId}")
    public ResponseEntity<Void> deleteHouse(@PathVariable Long houseId){
        houseService.delete(houseId);
        return ResponseEntity.noContent().build();
    }

}
