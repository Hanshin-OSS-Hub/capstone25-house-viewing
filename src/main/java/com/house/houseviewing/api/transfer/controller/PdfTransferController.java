package com.house.houseviewing.api.transfer.controller;

import com.house.houseviewing.api.transfer.service.PdfTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class PdfTransferController {

    private final PdfTransferService pdfTransferService;

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(){



        return ResponseEntity.ok().build();
    }
}
