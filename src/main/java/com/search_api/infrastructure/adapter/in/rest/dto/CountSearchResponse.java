package com.search_api.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record CountSearchResponse(
        String searchId,
        SearchData search,
        long count
) {
    public record SearchData(
            String hotelId,
            @JsonFormat(pattern = "dd/MM/yyyy") LocalDate checkIn,
            @JsonFormat(pattern = "dd/MM/yyyy") LocalDate checkOut,
            List<Integer> ages
    ) {
        public SearchData {
            ages = List.copyOf(ages);
        }
    }
}
