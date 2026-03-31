package com.search_api.domain.port.out;

import com.search_api.domain.model.Search;

public interface SearchEventPublisher {
    void publish(Search search);
}