package com.search_api.application.service;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.out.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SaveSearchServiceTest {

    private static final Search SEARCH = new Search(
            "id-1", "hotel1",
            LocalDate.of(2025, 1, 10),
            LocalDate.of(2025, 1, 15),
            List.of(30, 29)
    );

    @Mock
    private SearchRepository repository;

    private SaveSearchService service;

    @BeforeEach
    void setUp() {
        service = new SaveSearchService(repository);
    }

    @Test
    void save_shouldDelegateToRepository() {
        service.save(SEARCH);

        verify(repository).save(SEARCH);
    }
}