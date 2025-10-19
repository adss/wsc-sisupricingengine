package com.inditex.sisuprice.api.controller;

import com.inditex.sisuprice.domain.usecase.PriceQueryUseCase;
import com.inditex.sisuprice.api.dto.PriceResponse;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/prices")
@Validated
@Slf4j
public class PriceController {

    private final PriceQueryUseCase service;

    public PriceController(PriceQueryUseCase service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<PriceResponse> getPrice(
            @RequestParam @Min(1) int brandId,
            @RequestParam @Min(1) long productId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate) {

        log.info("request getPrice brandId={} productId={} date={}", brandId, productId, applicationDate);
        return service.query(brandId, productId, applicationDate)
                .map(response -> {
                    log.info("price found brandId={} productId={} priceList={} valid=[{}, {})", response.brandId(), response.productId(), response.priceList(), response.startDate(), response.endDate());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    log.debug("price not found brandId={} productId={} date={}", brandId, productId, applicationDate);
                    return ResponseEntity.notFound().build();
                });
    }
}