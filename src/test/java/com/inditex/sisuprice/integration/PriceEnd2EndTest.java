package com.inditex.sisuprice.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PriceEnd2EndTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Test 1: Request at 10:00 on day 14 for product 35455, brand 1")
    void test1() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("date", "2020-06-14T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50))
                .andExpect(jsonPath("$.curr").value("EUR"));
    }

    @Test
    @DisplayName("Test 2: Request at 16:00 on day 14 for product 35455, brand 1")
    void test2() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("date", "2020-06-14T16:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(35455))
                .andExpect(jsonPath("$.brandId").value(1))
                .andExpect(jsonPath("$.priceList").value(2))
                .andExpect(jsonPath("$.price").value(25.45));
    }

    @Test
    @DisplayName("Test 3: Request at 21:00 on day 14 for product 35455, brand 1")
    void test3() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("date", "2020-06-14T21:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(1))
                .andExpect(jsonPath("$.price").value(35.50));
    }

    @Test
    @DisplayName("Test 4: Request at 10:00 on day 15 for product 35455, brand 1")
    void test4() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("date", "2020-06-15T10:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(3))
                .andExpect(jsonPath("$.price").value(30.50));
    }

    @Test
    @DisplayName("Test 5: Request at 21:00 on day 16 for product 35455, brand 1")
    void test5() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("date", "2020-06-16T21:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priceList").value(4))
                .andExpect(jsonPath("$.price").value(38.95));
    }

    @Test
    @DisplayName("Returns 404 when no price is found")
    void notFound() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "99999")
                        .param("date", "2020-06-14T10:00:00"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Returns 400 when brandId is invalid")
    void invalidBrandId() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "0")
                        .param("productId", "35455")
                        .param("date", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Returns 400 when productId is invalid")
    void invalidProductId() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "0")
                        .param("date", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Returns 400 when date format is invalid")
    void invalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("productId", "35455")
                        .param("date", "invalid-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Returns 400 when required parameter is missing")
    void missingParameter() throws Exception {
        mockMvc.perform(get("/api/v1/prices")
                        .param("brandId", "1")
                        .param("date", "2020-06-14T10:00:00"))
                .andExpect(status().isBadRequest());
    }
}
