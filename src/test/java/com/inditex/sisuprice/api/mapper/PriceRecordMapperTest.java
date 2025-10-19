package com.inditex.sisuprice.api.mapper;

import com.inditex.sisuprice.api.dto.PriceResponse;
import com.inditex.sisuprice.domain.PriceRecord;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceRecordMapperTest {

    private final PriceRecordMapper mapper = Mappers.getMapper(PriceRecordMapper.class);

    @Test
    void mapsDomainToResponse() {
        PriceRecord d = new PriceRecord(
                1,
                LocalDateTime.parse("2020-06-14T10:00:00"),
                LocalDateTime.parse("2020-06-14T12:00:00"),
                2,
                35455L,
                5,
                new BigDecimal("25.45"),
                "EUR"
        );

        PriceResponse r = mapper.toResponse(d);
        assertNotNull(r);
        assertEquals(35455L, r.productId());
        assertEquals(1, r.brandId());
        assertEquals(2, r.priceList());
        assertEquals(LocalDateTime.parse("2020-06-14T10:00:00"), r.startDate());
        assertEquals(LocalDateTime.parse("2020-06-14T12:00:00"), r.endDate());
        assertEquals(new BigDecimal("25.45"), r.price());
        assertEquals("EUR", r.curr());
    }
}
