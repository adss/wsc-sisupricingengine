package com.inditex.sisuprice.integration;

import com.inditex.sisuprice.api.dto.PriceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PriceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @ParameterizedTest(name = "{index} => date={0}, expectedPriceList={1}, expectedPrice={2}")
    @MethodSource("priceScenarios")
    @DisplayName("Price scenarios for product 35455 and brand 1")
    void priceScenariosTest(String date, int expectedPriceList, BigDecimal expectedPrice) {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, date);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().productId()).isEqualTo(35455L);
        assertThat(response.getBody().brandId()).isEqualTo(1);
        assertThat(response.getBody().priceList()).isEqualTo(expectedPriceList);
        assertThat(response.getBody().price()).isEqualByComparingTo(expectedPrice);
    }

    static Stream<Arguments> priceScenarios() {
        return Stream.of(
                Arguments.of("2020-06-14T10:00:00", 1, new BigDecimal("35.50")),
                Arguments.of("2020-06-14T16:00:00", 2, new BigDecimal("25.45")),
                Arguments.of("2020-06-14T21:00:00", 1, new BigDecimal("35.50")),
                Arguments.of("2020-06-15T10:00:00", 3, new BigDecimal("30.50")),
                Arguments.of("2020-06-16T21:00:00", 4, new BigDecimal("38.95"))
        );
    }

    @Test
    @DisplayName("Returns 404 when product is not found")
    void returnsNotFoundWhenProductDoesNotExist() {
        ResponseEntity<PriceResponse> response = getPrice(1, 99999L, "2020-06-14T10:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Returns 404 when brand is not found")
    void returnsNotFoundWhenBrandDoesNotExist() {
        ResponseEntity<PriceResponse> response = getPrice(999, 35455L, "2020-06-14T10:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Returns 404 when date is out of range")
    void returnsNotFoundWhenDateOutOfRange() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2025-12-31T23:59:59");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest(name = "{index} => query='{0}'")
    @MethodSource("badRequestQueries")
    @DisplayName("Returns 400 for invalid or missing parameters")
    void returnsBadRequestForInvalidInputs(String query) {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/prices" + query,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    static Stream<String> badRequestQueries() {
        return Stream.of(
                "?brandId=0&productId=35455&date=2020-06-14T10:00:00",
                "?brandId=1&productId=0&date=2020-06-14T10:00:00",
                "?brandId=1&productId=35455&date=invalid-date",
                "?brandId=1&productId=35455"
        );
    }

    @Test
    @DisplayName("Handles boundary conditions correctly - start date inclusive")
    void handlesStartDateInclusive() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-14T15:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().priceList()).isEqualTo(2);
    }

    @Test
    @DisplayName("Handles boundary conditions correctly - end date exclusive")
    void handlesEndDateExclusive() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-14T18:30:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().priceList()).isEqualTo(1);
    }

    private ResponseEntity<PriceResponse> getPrice(int brandId, long productId, String date) {
        String url = String.format("/api/v1/prices?brandId=%d&productId=%d&date=%s",
                brandId, productId, date);
        return restTemplate.getForEntity(url, PriceResponse.class);
    }
}