package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.domain.registrysnapshot.dto.response.SnapshotResultResponse;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class RegistrySnapshotController {

    private final RegistrySnapshotService registrySnapshotService;

    @PostMapping("/register/{houseId}")
    public ResponseEntity<Long> register(
            @PathVariable Long houseId,
            @RequestPart("file") MultipartFile snapshot){
        Long register = registrySnapshotService.register(houseId, snapshot);
        return ResponseEntity.ok(register);
    }

    @GetMapping
    public ResponseEntity<List<SnapshotResultResponse>> getResults(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<SnapshotResultResponse> result = registrySnapshotService.getSnapshots(userDetails.getUserId());
        return ResponseEntity.ok(result);
    }
}
