package com.house.houseviewing.domain.registrysnapshot.controller;

import com.house.houseviewing.domain.registrysnapshot.service.RegistrySnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/snapshot")
@RequiredArgsConstructor
public class RegistrySnapshotController {

    private final RegistrySnapshotService registrySnapshotService;
}
