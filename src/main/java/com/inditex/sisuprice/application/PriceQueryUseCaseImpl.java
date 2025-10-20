package com.inditex.sisuprice.application;

import com.inditex.sisuprice.domain.PriceRecord;
import com.inditex.sisuprice.domain.repository.PriceRepository;
import com.inditex.sisuprice.domain.usecase.PriceQueryUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PriceQueryUseCaseImpl implements PriceQueryUseCase {

    private final PriceRepository repository;

    public PriceQueryUseCaseImpl(PriceRepository repository) {
        this.repository = repository;
    }

    @Override
    @Cacheable(value = "prices", key = "#brandId + '-' + #productId + '-' + #applicationDate")
    public Optional<PriceRecord> query(int brandId, long productId, LocalDateTime applicationDate) {

        long start = System.nanoTime();
        log.debug("usecase query brandId={} productId={} date={}", brandId, productId, applicationDate);

        Optional<PriceRecord> result = repository.findApplicable(brandId, productId, applicationDate);
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        if (result.isPresent()) {
            var r = result.get();
            log.debug("usecase result found brandId={} productId={} priceList={} tookMs={}",
                    brandId, productId, r.priceList(), tookMs);
        } else {
            log.debug("usecase result empty brandId={} productId={} tookMs={}", brandId, productId, tookMs);
        }

        return result;
    }
}
