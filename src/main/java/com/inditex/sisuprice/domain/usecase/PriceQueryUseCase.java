package com.inditex.sisuprice.domain.usecase;

import com.inditex.sisuprice.api.dto.PriceResponse;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * The interface PriceQuery use case.
 */
public interface PriceQueryUseCase {

    /**
     * Queries for the price details based on brand, product, and application date.
     *
     * @param brandId          the identifier of the brand
     * @param productId        the identifier of the product
     * @param applicationDate  the date and time for which the price information is requested
     * @return an Optional containing the price details if found, otherwise an empty Optional
     */
    Optional<PriceResponse> query(int brandId, long productId, LocalDateTime applicationDate);

}
