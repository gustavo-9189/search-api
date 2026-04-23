package com.search_api.infrastructure.adapter.in.rest;

import com.search_api.domain.exception.SearchNotFoundException;
import com.search_api.domain.model.Search;
import com.search_api.domain.model.SearchCount;
import com.search_api.domain.port.in.CountSearchUseCase;
import com.search_api.domain.port.in.CreateSearchUseCase;
import com.search_api.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@Import(GlobalExceptionHandler.class)
class SearchControllerTest {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final LocalDate CHECK_IN  = LocalDate.now().plusDays(10);
    private static final LocalDate CHECK_OUT = LocalDate.now().plusDays(15);
    private static final String CHECK_IN_STR  = CHECK_IN.format(FMT);
    private static final String CHECK_OUT_STR = CHECK_OUT.format(FMT);

    private static final String SEARCH_ID = "test-id";
    private static final String HOTEL_ID  = "1234aBc";
    private static final Search SEARCH = new Search(
            SEARCH_ID, HOTEL_ID,
            CHECK_IN,
            CHECK_OUT,
            List.of(30, 29, 1, 3)
    );

    private static final String VALID_REQUEST = """
            {
              "hotelId": "1234aBc",
              "checkIn": "%s",
              "checkOut": "%s",
              "ages": [30, 29, 1, 3]
            }
            """.formatted(CHECK_IN_STR, CHECK_OUT_STR);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSearchUseCase createSearchUseCase;

    @MockitoBean
    private CountSearchUseCase countSearchUseCase;

    @Test
    void postSearch_shouldReturn200WithSearchId() throws Exception {
        when(createSearchUseCase.create(eq(HOTEL_ID), any(), any(), any())).thenReturn(SEARCH_ID);

        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value(SEARCH_ID));
    }

    @Test
    void postSearch_shouldReturn400_whenHotelIdIsBlank() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotelId": "", "checkIn": "%s", "checkOut": "%s", "ages": [30]}
                                """.formatted(CHECK_IN_STR, CHECK_OUT_STR)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearch_shouldReturn400_whenCheckInIsNull() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotelId": "hotel1", "checkOut": "%s", "ages": [30]}
                                """.formatted(CHECK_OUT_STR)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearch_shouldReturn400_whenCheckInIsInPast() throws Exception {
        String pastDate = LocalDate.now().minusDays(1).format(FMT);
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotelId": "hotel1", "checkIn": "%s", "checkOut": "%s", "ages": [30]}
                                """.formatted(pastDate, CHECK_OUT_STR)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearch_shouldReturn400_whenCheckOutBeforeCheckIn() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotelId": "hotel1", "checkIn": "%s", "checkOut": "%s", "ages": [30]}
                                """.formatted(CHECK_OUT_STR, CHECK_IN_STR)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postSearch_shouldReturn400_whenAgesIsEmpty() throws Exception {
        mockMvc.perform(post("/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"hotelId": "hotel1", "checkIn": "%s", "checkOut": "%s", "ages": []}
                                """.formatted(CHECK_IN_STR, CHECK_OUT_STR)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCount_shouldReturn200WithCountData() throws Exception {
        when(countSearchUseCase.count(SEARCH_ID)).thenReturn(new SearchCount(SEARCH_ID, SEARCH, 100L));

        mockMvc.perform(get("/count").param("searchId", SEARCH_ID))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.searchId").value(SEARCH_ID),
                        jsonPath("$.count").value(100),
                        jsonPath("$.search.hotelId").value(HOTEL_ID),
                        jsonPath("$.search.checkIn").value(CHECK_IN_STR),
                        jsonPath("$.search.checkOut").value(CHECK_OUT_STR)
                );
    }

    @Test
    void getCount_shouldReturn404_whenSearchIdNotFound() throws Exception {
        when(countSearchUseCase.count("unknown")).thenThrow(new SearchNotFoundException("unknown"));

        mockMvc.perform(get("/count").param("searchId", "unknown"))
                .andExpect(status().isNotFound());
    }
}