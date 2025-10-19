package com.inditex.sisuprice.infrastructure.persistence;

import com.inditex.sisuprice.domain.PriceRecord;
import com.inditex.sisuprice.infrastructure.mapper.PriceEntityMapper;
import com.inditex.sisuprice.infrastructure.persistence.jpa.PriceEntity;
import com.inditex.sisuprice.infrastructure.persistence.jpa.PriceJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceDbRepositoryTest {

    @Mock
    PriceJpaRepository jpaRepository;
    @Mock
    PriceEntityMapper mapper;

    @InjectMocks
    PriceDbRepository repository;

    private PriceEntity entity(long id) {
        PriceEntity e = new PriceEntity();
        e.setId(id);
        e.setBrandId(1);
        e.setStartDate(LocalDateTime.parse("2020-06-14T10:00:00"));
        e.setEndDate(LocalDateTime.parse("2020-06-14T12:00:00"));
        e.setPriceList(2);
        e.setProductId(35455L);
        e.setPriority(5);
        e.setPrice(new BigDecimal("25.45"));
        e.setCurr("EUR");
        return e;
    }

    private PriceRecord domain() {
        return new PriceRecord(
                1,
                LocalDateTime.parse("2020-06-14T10:00:00"),
                LocalDateTime.parse("2020-06-14T12:00:00"),
                2,
                35455L,
                5,
                new BigDecimal("25.45"),
                "EUR"
        );
    }

    @BeforeEach
    void resetMocks() {
        clearInvocations(jpaRepository, mapper);
    }

    @Test
    void findAllMapsAll() {
        var e1 = entity(1L);
        var e2 = entity(2L);
        when(jpaRepository.findAll()).thenReturn(List.of(e1, e2));
        when(mapper.toDomain(e1)).thenReturn(domain());
        when(mapper.toDomain(e2)).thenReturn(domain());

        var list = repository.findAll();

        assertEquals(2, list.size());
        verify(jpaRepository).findAll();
        verify(mapper, times(2)).toDomain(any());
    }

    @Test
    void findApplicableDelegatesToJpaAndMapsFirst() {
        var e = entity(1L);
        when(jpaRepository.findTopApplicable(1, 35455L, LocalDateTime.parse("2020-06-14T10:00:00")))
                .thenReturn(List.of(e));
        when(mapper.toDomain(e)).thenReturn(domain());

        Optional<PriceRecord> result = repository.findApplicable(1, 35455L, LocalDateTime.parse("2020-06-14T10:00:00"));

        assertTrue(result.isPresent());
        assertEquals(35455L, result.get().productId());
        verify(jpaRepository).findTopApplicable(1, 35455L, LocalDateTime.parse("2020-06-14T10:00:00"));
        verify(mapper).toDomain(e);
    }

    @Test
    void findApplicableReturnsEmptyWhenJpaReturnsEmpty() {
        when(jpaRepository.findTopApplicable(anyInt(), anyLong(), any())).thenReturn(List.of());
        var result = repository.findApplicable(9, 9L, LocalDateTime.parse("2020-01-01T00:00:00"));
        assertTrue(result.isEmpty());
    }
}
