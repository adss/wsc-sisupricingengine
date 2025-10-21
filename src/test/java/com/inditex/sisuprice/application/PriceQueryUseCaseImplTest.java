package com.inditex.sisuprice.application;

import com.inditex.sisuprice.domain.PriceRecord;
import com.inditex.sisuprice.domain.repository.PriceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PriceQueryUseCaseImplTest {

    @Mock
    PriceRepository repository;

    @InjectMocks
    PriceQueryUseCaseImpl useCase;

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

    @Test
    void returnsRecordWhenRepositoryFindsRecord() {
        var date = LocalDateTime.parse("2020-06-14T10:00:00");
        var priceRecord = domain();

        when(repository.findApplicable(1, 35455L, date)).thenReturn(Optional.of(priceRecord));

        var result = useCase.query(1, 35455L, date);

        assertTrue(result.isPresent());
        assertEquals(35455L, result.get().productId());
        assertEquals(1, result.get().brandId());
        assertEquals(2, result.get().priceList());
        assertEquals(new BigDecimal("25.45"), result.get().price());
        assertEquals("EUR", result.get().curr());

        verify(repository).findApplicable(1, 35455L, date);
    }

    @Test
    void returnsEmptyWhenRepositoryReturnsEmpty() {
        var date = LocalDateTime.parse("2020-06-14T10:00:00");
        when(repository.findApplicable(1, 35455L, date)).thenReturn(Optional.empty());

        var result = useCase.query(1, 35455L, date);

        assertTrue(result.isEmpty());
        verify(repository).findApplicable(1, 35455L, date);
    }

    @Test
    void queryDelegatesToRepository() {
        var date = LocalDateTime.parse("2020-06-14T16:00:00");
        when(repository.findApplicable(anyInt(), anyLong(), any())).thenReturn(Optional.empty());

        useCase.query(99, 888L, date);

        verify(repository).findApplicable(99, 888L, date);
    }
}
