package com.search_api.domain.model;

public record SearchCount(
        String searchId,
        Search search,
        long count
) {}