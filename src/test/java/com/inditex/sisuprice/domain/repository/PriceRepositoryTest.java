package com.inditex.sisuprice.domain.repository;

import com.inditex.sisuprice.domain.PriceRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PriceRepositoryTest {

    private record InMemoryPriceRepository(List<PriceRecord> data) implements PriceRepository {

        @Override
            public List<PriceRecord> findAll() {
                return data;
            }
        }

    private static PriceRecord rec(int brandId, long productId, String start, String end,
                                   int priceList, int priority, double price, String curr) {
        return new PriceRecord(
                brandId,
                LocalDateTime.parse(start),
                LocalDateTime.parse(end),
                priceList,
                productId,
                priority,
                BigDecimal.valueOf(price),
                curr
        );
    }

    @Nested
    class FindApplicable {
        @Test
        @DisplayName("returns empty when no records match brand/product")
        void noMatch() {
            var repo = new InMemoryPriceRepository(List.of(
                    rec(1, 100L, "2020-06-14T00:00:00", "2020-12-31T23:59:59",
                            1, 0, 35.50, "EUR")
            ));

            Optional<PriceRecord> result = repo.findApplicable(2, 100L,
                    LocalDateTime.parse("2020-06-14T10:00:00"));
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("includes startDate and excludes endDate conditions")
        void boundaries() {
            var r = rec(1, 100L, "2020-06-14T10:00:00", "2020-06-14T12:00:00",
                    1, 0, 10.0, "EUR");
            var repo = new InMemoryPriceRepository(List.of(r));

            // included
            assertTrue(repo.findApplicable(1, 100L, LocalDateTime.parse("2020-06-14T10:00:00")).isPresent());
            // excluded
            assertTrue(repo.findApplicable(1, 100L, LocalDateTime.parse("2020-06-14T12:00:00")).isEmpty());
        }

        @Test
        @DisplayName("selects highest priority when multiple records apply")
        void highestPriority() {
            var rLow = rec(1, 100L, "2020-06-14T00:00:00", "2020-06-15T00:00:00",
                    1, 0, 35.50, "EUR");
            var rHigh = rec(1, 100L, "2020-06-14T00:00:00", "2020-06-15T00:00:00",
                    2, 5, 25.45, "EUR");
            var repo = new InMemoryPriceRepository(List.of(rLow, rHigh));

            var result = repo.findApplicable(1, 100L, LocalDateTime.parse("2020-06-14T16:00:00"));
            assertTrue(result.isPresent());
            assertEquals(2, result.get().priceList());
            assertEquals(5, result.get().priority());
        }

        @Test
        @DisplayName("filters by product and brand correctly")
        void filterByBrandAndProduct() {
            var r1 = rec(1, 100L, "2020-01-01T00:00:00", "2021-01-01T00:00:00",
                    1, 0, 10, "EUR");
            var r2 = rec(1, 101L, "2020-01-01T00:00:00", "2021-01-01T00:00:00",
                    2, 1, 20, "EUR");
            var r3 = rec(2, 100L, "2020-01-01T00:00:00", "2021-01-01T00:00:00",
                    3, 2, 30, "EUR");
            var repo = new InMemoryPriceRepository(List.of(r1, r2, r3));

            var result = repo.findApplicable(1, 100L, LocalDateTime.parse("2020-06-14T16:00:00"));
            assertTrue(result.isPresent());
            assertEquals(1, result.get().priceList());
        }
    }
}
