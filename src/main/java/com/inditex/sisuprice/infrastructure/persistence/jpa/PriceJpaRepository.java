package com.inditex.sisuprice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceJpaRepository extends JpaRepository<PriceEntity, Long> {

    @Query("""
            SELECT p FROM PriceEntity p 
            WHERE p.brandId = :brandId 
              AND p.productId = :productId 
              AND p.startDate <= :applicationDate 
              AND p.endDate > :applicationDate 
            ORDER BY p.priority DESC 
            LIMIT 1
            """)
    Optional<PriceEntity> findTopApplicable(@Param("brandId") int brandId,
                                             @Param("productId") long productId,
                                             @Param("applicationDate") LocalDateTime applicationDate);
}