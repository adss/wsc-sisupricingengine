package com.inditex.sisuprice.application;

import com.inditex.sisuprice.api.dto.PriceResponse;
import com.inditex.sisuprice.api.mapper.PriceRecordMapper;
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
    @Mock
    PriceRecordMapper mapper;

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
    void returnsMappedResponseWhenRepositoryFindsRecord() {
        var date = LocalDateTime.parse("2020-06-14T10:00:00");
        var record = domain();
        var response = new PriceResponse(35455L, 1, 2, record.startDate(), record.endDate(), record.price(), record.curr());

        when(repository.findApplicable(1, 35455L, date)).thenReturn(Optional.of(record));
        when(mapper.toResponse(record)).thenReturn(response);

        var result = useCase.query(1, 35455L, date);
        assertTrue(result.isPresent());
        assertEquals(response, result.get());

        verify(repository).findApplicable(1, 35455L, date);
        verify(mapper).toResponse(record);
    }

    @Test
    void returnsEmptyWhenRepositoryReturnsEmpty() {
        var date = LocalDateTime.parse("2020-06-14T10:00:00");
        when(repository.findApplicable(1, 35455L, date)).thenReturn(Optional.empty());

        var result = useCase.query(1, 35455L, date);
        assertTrue(result.isEmpty());
        verify(repository).findApplicable(1, 35455L, date);
        verifyNoInteractions(mapper);
    }
}
