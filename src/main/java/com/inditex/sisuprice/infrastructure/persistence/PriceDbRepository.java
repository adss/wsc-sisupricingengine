package com.inditex.sisuprice.infrastructure.persistence;

import com.inditex.sisuprice.domain.PriceRecord;
import com.inditex.sisuprice.domain.repository.PriceRepository;
import com.inditex.sisuprice.infrastructure.persistence.jpa.PriceJpaRepository;
import com.inditex.sisuprice.infrastructure.mapper.PriceEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class PriceDbRepository implements PriceRepository {

    private final PriceJpaRepository jpaRepository;
    private final PriceEntityMapper mapper;

    public PriceDbRepository(PriceJpaRepository jpaRepository, PriceEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<PriceRecord> findAll() {
        log.debug("db findAll");
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PriceRecord> findApplicable(int brandId, long productId, LocalDateTime applicationDate) {
        log.debug("db findApplicable brandId={} productId={} date={}", brandId, productId, applicationDate);
        Optional<PriceRecord> result = jpaRepository.findTopApplicable(brandId, productId, applicationDate)
                .stream()
                .findFirst()
                .map(mapper::toDomain);
        log.debug("db findApplicable resultPresent={} brandId={} productId={} date={}", result.isPresent(), brandId, productId, applicationDate);
        return result;
    }
}