package com.search_api.domain.port.in;

import com.search_api.domain.model.Search;

public interface SaveSearchUseCase {
    void save(Search search);
}