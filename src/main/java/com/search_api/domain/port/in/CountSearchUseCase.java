package com.search_api.domain.port.in;

import com.search_api.domain.model.SearchCount;

public interface CountSearchUseCase {
    SearchCount count(String searchId);
}