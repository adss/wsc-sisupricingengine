package com.inditex.sisuprice.web;

import com.inditex.sisuprice.PriceApplication;
import com.inditex.sisuprice.api.dto.PriceResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = PriceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PriceControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    private String url(String query) {
        return "http://localhost:" + port + "/api/v1/prices" + query;
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2020-06-14T10:00:00", // Test 1
            "2020-06-14T16:00:00", // Test 2
            "2020-06-14T21:00:00", // Test 3
            "2020-06-15T10:00:00", // Test 4
            "2020-06-16T21:00:00"  // Test 5
    })
    void shouldReturnCorrectPriceForDifferentDates(String dateTime) {
        String formattedDate = LocalDateTime.parse(dateTime).format(DateTimeFormatter.ISO_DATE_TIME);

        PriceResponse r = restTemplate.getForObject(
                url("?brandId=1&productId=35455&date=" + formattedDate),
                PriceResponse.class
        );

        assertThat(r).isNotNull();
        assertThat(r.brandId()).isEqualTo(1);
        assertThat(r.productId()).isEqualTo(35455);
        assertThat(r.curr()).isEqualTo("EUR");

        switch (dateTime) {
            case "2020-06-14T10:00:00" -> {
                assertThat(r.priceList()).isEqualTo(1);
                assertThat(r.price()).isEqualByComparingTo(new BigDecimal("35.50"));
            }
            case "2020-06-14T16:00:00" -> {
                assertThat(r.priceList()).isEqualTo(2);
                assertThat(r.price()).isEqualByComparingTo(new BigDecimal("25.45"));
            }
            case "2020-06-14T21:00:00" -> {
                assertThat(r.priceList()).isEqualTo(1);
                assertThat(r.price()).isEqualByComparingTo(new BigDecimal("35.50"));
            }
            case "2020-06-15T10:00:00" -> {
                assertThat(r.priceList()).isEqualTo(3);
                assertThat(r.price()).isEqualByComparingTo(new BigDecimal("30.50"));
            }
            case "2020-06-16T21:00:00" -> {
                assertThat(r.priceList()).isEqualTo(4);
                assertThat(r.price()).isEqualByComparingTo(new BigDecimal("38.95"));
            }
            default -> throw new IllegalArgumentException("Unexpected date: " + dateTime);
        }
    }

    @Test
    void shouldReturnHighestPriorityWhenMultipleMatch() {
        String date = LocalDateTime.of(2020, 6, 14, 16, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
        ResponseEntity<PriceResponse> response = restTemplate.getForEntity(url("?brandId=1&productId=35455&date=" + date), PriceResponse.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        PriceResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.brandId()).isEqualTo(1);
        assertThat(body.productId()).isEqualTo(35455);
        assertThat(body.priceList()).isEqualTo(2);
        assertThat(body.price()).isEqualByComparingTo(new BigDecimal("25.45"));
        assertThat(body.curr()).isEqualTo("EUR");
    }

    @Test
    void shouldReturnNotFoundForUnknownProduct() {
        String date = LocalDateTime.of(2020, 6, 14, 16, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
        ResponseEntity<String> response = restTemplate.getForEntity(url("?brandId=1&productId=99999&date=" + date), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(404);
    }

    @Test
    void shouldReturnOtherApplicableTariffsAcrossDates() {
        // 2020-06-15 10:00 -> priceList 3
        String date1 = LocalDateTime.of(2020, 6, 15, 10, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
        PriceResponse r1 = restTemplate.getForObject(url("?brandId=1&productId=35455&date=" + date1), PriceResponse.class);
        assertThat(r1).isNotNull();
        assertThat(r1.priceList()).isEqualTo(3);
        assertThat(r1.price()).isEqualByComparingTo(new BigDecimal("30.50"));

        // 2020-06-16 00:00 -> priceList 4
        String date2 = LocalDateTime.of(2020, 6, 16, 0, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
        PriceResponse r2 = restTemplate.getForObject(url("?brandId=1&productId=35455&date=" + date2), PriceResponse.class);
        assertThat(r2).isNotNull();
        assertThat(r2.priceList()).isEqualTo(4);
        assertThat(r2.price()).isEqualByComparingTo(new BigDecimal("38.95"));
    }

    @Test
    void shouldReturnBadRequestForInvalidBrandId() {
        String date = LocalDateTime.of(2020, 6, 14, 10, 0, 0).format(DateTimeFormatter.ISO_DATE_TIME);
        ResponseEntity<String> response = restTemplate.getForEntity(url("?brandId=0&productId=35455&date=" + date), String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("messages");
    }
}
