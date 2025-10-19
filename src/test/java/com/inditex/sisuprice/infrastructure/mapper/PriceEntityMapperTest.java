package com.inditex.sisuprice.infrastructure.mapper;

import com.inditex.sisuprice.domain.PriceRecord;
import com.inditex.sisuprice.infrastructure.persistence.jpa.PriceEntity;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PriceEntityMapperTest {

    private final PriceEntityMapper mapper = Mappers.getMapper(PriceEntityMapper.class);

    @Test
    void mapsAllFieldsFromEntityToDomain() {
        PriceEntity e = new PriceEntity();
        e.setId(10L);
        e.setBrandId(1);
        e.setStartDate(LocalDateTime.parse("2020-06-14T10:00:00"));
        e.setEndDate(LocalDateTime.parse("2020-06-14T12:00:00"));
        e.setPriceList(2);
        e.setProductId(35455L);
        e.setPriority(5);
        e.setPrice(new BigDecimal("25.45"));
        e.setCurr("EUR");

        PriceRecord d = mapper.toDomain(e);

        assertNotNull(d);
        assertEquals(1, d.brandId());
        assertEquals(LocalDateTime.parse("2020-06-14T10:00:00"), d.startDate());
        assertEquals(LocalDateTime.parse("2020-06-14T12:00:00"), d.endDate());
        assertEquals(2, d.priceList());
        assertEquals(35455L, d.productId());
        assertEquals(5, d.priority());
        assertEquals(new BigDecimal("25.45"), d.price());
        assertEquals("EUR", d.curr());
    }
}
