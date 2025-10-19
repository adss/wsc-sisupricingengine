package com.inditex.sisuprice.application;

import com.inditex.sisuprice.api.dto.PriceResponse;
import com.inditex.sisuprice.api.mapper.PriceRecordMapper;
import com.inditex.sisuprice.domain.repository.PriceRepository;
import com.inditex.sisuprice.domain.usecase.PriceQueryUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class PriceQueryUseCaseImpl implements PriceQueryUseCase {

    private final PriceRepository repository;
    private final PriceRecordMapper mapper;

    public PriceQueryUseCaseImpl(PriceRepository repository, PriceRecordMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<PriceResponse> query(int brandId, long productId, LocalDateTime applicationDate) {
        long start = System.nanoTime();
        log.debug("usecase query brandId={} productId={} date={}", brandId, productId, applicationDate);
        Optional<PriceResponse> result = repository.findApplicable(brandId, productId, applicationDate)
                .map(mapper::toResponse);
        long tookMs = (System.nanoTime() - start) / 1_000_000;
        if (result.isPresent()) {
            log.debug("usecase result found brandId={} productId={} priceList={} tookMs={}",
                    brandId, productId, result.get().priceList(), tookMs);
        } else {
            log.debug("usecase result empty brandId={} productId={} tookMs={}", brandId, productId, tookMs);
        }
        return result;
    }
}
