package com.search_api.infrastructure.adapter.out.persistence;

import com.search_api.domain.model.Search;
import com.search_api.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchPersistenceAdapterTest {

    private static final String SEARCH_ID  = "id-1";
    private static final String HOTEL_ID   = "hotel1";
    private static final LocalDate CHECK_IN  = LocalDate.of(2023, 12, 29);
    private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);
    private static final List<Integer> AGES = List.of(30, 29, 1, 3);

    private static final Search SEARCH = new Search(SEARCH_ID, HOTEL_ID, CHECK_IN, CHECK_OUT, AGES);
    private static final SearchEntity ENTITY = new SearchEntity(SEARCH_ID, HOTEL_ID, CHECK_IN, CHECK_OUT, AGES);

    @Mock
    private SearchJpaRepository jpaRepository;

    private SearchPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new SearchPersistenceAdapter(jpaRepository);
    }

    @Test
    void save_shouldPersistSearchEntityWithCorrectFields() {
        adapter.save(SEARCH);

        ArgumentCaptor<SearchEntity> captor = ArgumentCaptor.forClass(SearchEntity.class);
        verify(jpaRepository).save(captor.capture());
        SearchEntity entity = captor.getValue();

        assertAll(
                () -> assertThat(entity.getSearchId()).isEqualTo(SEARCH_ID),
                () -> assertThat(entity.getHotelId()).isEqualTo(HOTEL_ID),
                () -> assertThat(entity.getCheckIn()).isEqualTo(CHECK_IN),
                () -> assertThat(entity.getCheckOut()).isEqualTo(CHECK_OUT),
                () -> assertThat(entity.getAges()).isEqualTo(AGES)
        );
    }

    @Test
    void findBySearchId_shouldReturnMappedDomain_whenFound() {
        when(jpaRepository.findById(SEARCH_ID)).thenReturn(Optional.of(ENTITY));

        Optional<Search> result = adapter.findBySearchId(SEARCH_ID);

        assertAll(
                () -> assertThat(result).isPresent(),
                () -> assertThat(result.get().searchId()).isEqualTo(SEARCH_ID),
                () -> assertThat(result.get().hotelId()).isEqualTo(HOTEL_ID),
                () -> assertThat(result.get().ages()).isEqualTo(AGES)
        );
    }

    @Test
    void findBySearchId_shouldReturnEmpty_whenNotFound() {
        when(jpaRepository.findById("missing")).thenReturn(Optional.empty());

        assertThat(adapter.findBySearchId("missing")).isEmpty();
    }

    @Test
    void countBySearchFields_shouldCountOnlyMatchingAges() {
        SearchEntity sameAges    = new SearchEntity("id-1", HOTEL_ID, CHECK_IN, CHECK_OUT, List.of(30, 29, 1, 3));
        SearchEntity sameAges2   = new SearchEntity("id-2", HOTEL_ID, CHECK_IN, CHECK_OUT, List.of(30, 29, 1, 3));
        SearchEntity differentOrder = new SearchEntity("id-3", HOTEL_ID, CHECK_IN, CHECK_OUT, List.of(3, 1, 29, 30));
        SearchEntity differentAges  = new SearchEntity("id-4", HOTEL_ID, CHECK_IN, CHECK_OUT, List.of(25));

        when(jpaRepository.findByHotelIdAndCheckInAndCheckOut(HOTEL_ID, CHECK_IN, CHECK_OUT))
                .thenReturn(List.of(sameAges, sameAges2, differentOrder, differentAges));

        assertThat(adapter.countBySearchFields(SEARCH)).isEqualTo(2L);
    }

    @Test
    void countBySearchFields_shouldReturnZero_whenNoMatch() {
        when(jpaRepository.findByHotelIdAndCheckInAndCheckOut(HOTEL_ID, CHECK_IN, CHECK_OUT))
                .thenReturn(List.of());

        assertThat(adapter.countBySearchFields(SEARCH)).isZero();
    }
}