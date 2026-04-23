package com.search_api.application.service;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.in.SaveSearchUseCase;
import com.search_api.domain.port.out.SearchRepository;

public class SaveSearchService implements SaveSearchUseCase {

    private final SearchRepository repository;

    public SaveSearchService(SearchRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Search search) {
        repository.save(search);
    }
}