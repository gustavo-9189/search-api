package com.search_api.domain.port.out;

import com.search_api.domain.model.Search;

import java.util.Optional;

public interface SearchRepository {
    void save(Search search);
    Optional<Search> findBySearchId(String searchId);
    long countBySearchFields(Search search);
}