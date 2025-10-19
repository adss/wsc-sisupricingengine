package com.inditex.sisuprice.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain model representing a row in PRICES table.
 */
public record PriceRecord(
        int brandId,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int priceList,
        long productId,
        int priority,
        BigDecimal price,
        String curr
) {}