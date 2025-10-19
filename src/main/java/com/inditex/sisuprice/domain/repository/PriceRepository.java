package com.inditex.sisuprice.domain.repository;

import com.inditex.sisuprice.domain.PriceRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Port for querying prices.
 */
public interface PriceRepository {

    List<PriceRecord> findAll();

    default Optional<PriceRecord> findApplicable(int brandId, long productId, LocalDateTime applicationDate) {
        return findAll().stream()
                .filter(p -> p.brandId() == brandId)
                .filter(p -> p.productId() == productId)
                .filter(p -> !applicationDate.isBefore(p.startDate()) && applicationDate.isBefore(p.endDate()))
                .min((a, b) -> Integer.compare(b.priority(), a.priority()));
    }
}
