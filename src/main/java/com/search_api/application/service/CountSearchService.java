package com.search_api.application.service;

import com.search_api.domain.exception.SearchNotFoundException;
import com.search_api.domain.model.Search;
import com.search_api.domain.model.SearchCount;
import com.search_api.domain.port.in.CountSearchUseCase;
import com.search_api.domain.port.out.SearchRepository;

public class CountSearchService implements CountSearchUseCase {

    private final SearchRepository repository;

    public CountSearchService(SearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public SearchCount count(String searchId) {
        Search search = repository.findBySearchId(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));
        long count = repository.countBySearchFields(search);
        return new SearchCount(searchId, search, count);
    }
}