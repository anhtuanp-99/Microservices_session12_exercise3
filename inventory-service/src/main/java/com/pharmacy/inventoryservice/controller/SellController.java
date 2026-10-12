package com.pharmacy.inventoryservice.controller;

import com.pharmacy.inventoryservice.dto.request.SellRequest;
import com.pharmacy.inventoryservice.dto.response.SellResponse;
import com.pharmacy.inventoryservice.service.SellService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sell")
@RequiredArgsConstructor
public class SellController {

    private final SellService sellService;

    @PostMapping
    public ResponseEntity<SellResponse> sell(@Valid @RequestBody SellRequest request) {
        return ResponseEntity.ok(sellService.sell(request));
    }
}