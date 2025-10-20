package com.inditex.sisuprice.domain.repository;

import com.inditex.sisuprice.domain.PriceRecord;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Port for querying prices.
 */
public interface PriceRepository {

    /**
     * Find all price records.
     */
    List<PriceRecord> findAll();

    /**
     * Find the applicable price for the given criteria.
     * Implementation should apply business rules (date range, priority).
     */
    Optional<PriceRecord> findApplicable(int brandId, long productId, LocalDateTime applicationDate);
}
