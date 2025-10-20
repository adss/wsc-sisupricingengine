package com.inditex.sisuprice.domain.usecase;

import com.inditex.sisuprice.domain.PriceRecord;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The interface PriceQuery use case.
 */
public interface PriceQueryUseCase {

    /**
     * Queries the price information for a specific product and brand at a given application date.
     *
     * @param brandId the ID of the brand associated with the product
     * @param productId the ID of the product for which price is being queried
     * @param applicationDate the date and time when the price should be applicable
     * @return an Optional containing the price response if an applicable price is found, or an empty Optional otherwise
     */
    Optional<PriceRecord> query(int brandId, long productId, LocalDateTime applicationDate);

}
