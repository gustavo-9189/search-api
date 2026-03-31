package com.search_api.domain.exception;

public class SearchNotFoundException extends RuntimeException {

    public SearchNotFoundException(String searchId) {
        super("No se encontró una búsqueda con id: %s".formatted(searchId));
    }
}