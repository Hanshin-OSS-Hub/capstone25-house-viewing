package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.domain.registrysnapshot.dto.response.SnapshotResultResponse;
import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import com.house.houseviewing.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class RegistrySnapshotController {

    private final RegistrySnapshotService registrySnapshotService;

    @GetMapping
    public ResponseEntity<List<SnapshotResultResponse>> getResults(
            @AuthenticationPrincipal CustomUserDetails userDetails){
        List<SnapshotResultResponse> result = registrySnapshotService.getSnapshots(userDetails.getUserId());
        return ResponseEntity.ok(result);
    }
}
