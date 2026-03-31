package com.search_api.application.service;

import com.search_api.domain.exception.SearchNotFoundException;
import com.search_api.domain.model.Search;
import com.search_api.domain.model.SearchCount;
import com.search_api.domain.port.out.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountSearchServiceTest {

    private static final String SEARCH_ID = "test-id";
    private static final Search SEARCH = new Search(
            SEARCH_ID, "1234aBc",
            LocalDate.of(2023, 12, 29),
            LocalDate.of(2023, 12, 31),
            List.of(30, 29, 1, 3)
    );
    private static final long COUNT = 5L;

    @Mock
    private SearchRepository repository;

    private CountSearchService service;

    @BeforeEach
    void setUp() {
        service = new CountSearchService(repository);
    }

    @Test
    void count_shouldReturnSearchCountWithCorrectValues() {
        when(repository.findBySearchId(SEARCH_ID)).thenReturn(Optional.of(SEARCH));
        when(repository.countBySearchFields(SEARCH)).thenReturn(COUNT);

        SearchCount result = service.count(SEARCH_ID);

        assertAll(
                () -> assertThat(result.searchId()).isEqualTo(SEARCH_ID),
                () -> assertThat(result.search()).isEqualTo(SEARCH),
                () -> assertThat(result.count()).isEqualTo(COUNT)
        );
    }

    @Test
    void count_shouldThrowSearchNotFoundException_whenSearchIdNotFound() {
        when(repository.findBySearchId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.count("unknown"))
                .isInstanceOf(SearchNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}