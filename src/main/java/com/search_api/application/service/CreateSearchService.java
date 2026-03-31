package com.search_api.application.service;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.in.CreateSearchUseCase;
import com.search_api.domain.port.out.SearchEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CreateSearchService implements CreateSearchUseCase {

    private final SearchEventPublisher publisher;

    public CreateSearchService(SearchEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public String create(String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {
        String searchId = UUID.randomUUID().toString();
        Search search = new Search(searchId, hotelId, checkIn, checkOut, ages);
        publisher.publish(search);
        return searchId;
    }
}