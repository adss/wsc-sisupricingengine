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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PriceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Test 1: Request at 10:00 on day 14 for product 35455 and brand 1")
    void test1_At10AM_Day14() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-14T10:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().productId()).isEqualTo(35455L);
        assertThat(response.getBody().brandId()).isEqualTo(1);
        assertThat(response.getBody().priceList()).isEqualTo(1);
        assertThat(response.getBody().price()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    @DisplayName("Test 2: Request at 16:00 on day 14 for product 35455 and brand 1")
    void test2_At4PM_Day14() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-14T16:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().productId()).isEqualTo(35455L);
        assertThat(response.getBody().brandId()).isEqualTo(1);
        assertThat(response.getBody().priceList()).isEqualTo(2);
        assertThat(response.getBody().price()).isEqualByComparingTo(new BigDecimal("25.45"));
    }

    @Test
    @DisplayName("Test 3: Request at 21:00 on day 14 for product 35455 and brand 1")
    void test3_At9PM_Day14() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-14T21:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().productId()).isEqualTo(35455L);
        assertThat(response.getBody().brandId()).isEqualTo(1);
        assertThat(response.getBody().priceList()).isEqualTo(1);
        assertThat(response.getBody().price()).isEqualByComparingTo(new BigDecimal("35.50"));
    }

    @Test
    @DisplayName("Test 4: Request at 10:00 on day 15 for product 35455 and brand 1")
    void test4_At10AM_Day15() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-15T10:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().productId()).isEqualTo(35455L);
        assertThat(response.getBody().brandId()).isEqualTo(1);
        assertThat(response.getBody().priceList()).isEqualTo(3);
        assertThat(response.getBody().price()).isEqualByComparingTo(new BigDecimal("30.50"));
    }

    @Test
    @DisplayName("Test 5: Request at 21:00 on day 16 for product 35455 and brand 1")
    void test5_At9PM_Day16() {
        ResponseEntity<PriceResponse> response = getPrice(1, 35455L, "2020-06-16T21:00:00");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().productId()).isEqualTo(35455L);
        assertThat(response.getBody().brandId()).isEqualTo(1);
        assertThat(response.getBody().priceList()).isEqualTo(4);
        assertThat(response.getBody().price()).isEqualByComparingTo(new BigDecimal("38.95"));
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

    @Test
    @DisplayName("Returns 400 when brandId is invalid (0)")
    void returnsBadRequestWhenBrandIdIsZero() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/prices?brandId=0&productId=35455&date=2020-06-14T10:00:00",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Returns 400 when productId is invalid (0)")
    void returnsBadRequestWhenProductIdIsZero() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/prices?brandId=1&productId=0&date=2020-06-14T10:00:00",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Returns 400 when date format is invalid")
    void returnsBadRequestWhenDateFormatIsInvalid() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/prices?brandId=1&productId=35455&date=invalid-date",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Returns 400 when required parameter is missing")
    void returnsBadRequestWhenParameterIsMissing() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/prices?brandId=1&productId=35455",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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