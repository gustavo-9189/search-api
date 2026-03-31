package com.search_api.domain.port.in;

import java.time.LocalDate;
import java.util.List;

public interface CreateSearchUseCase {
    String create(String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages);
}