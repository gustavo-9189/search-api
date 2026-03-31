package com.search_api.application.service;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.out.SearchEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CreateSearchServiceTest {

    private static final String HOTEL_ID   = "1234aBc";
    private static final LocalDate CHECK_IN  = LocalDate.of(2023, 12, 29);
    private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);
    private static final List<Integer> AGES = List.of(30, 29, 1, 3);

    @Mock
    private SearchEventPublisher publisher;

    private CreateSearchService service;

    @BeforeEach
    void setUp() {
        service = new CreateSearchService(publisher);
    }

    @Test
    void create_shouldReturnValidSearchIdAndPublishCorrectEvent() {
        String searchId = service.create(HOTEL_ID, CHECK_IN, CHECK_OUT, AGES);

        ArgumentCaptor<Search> captor = ArgumentCaptor.forClass(Search.class);
        verify(publisher).publish(captor.capture());
        Search published = captor.getValue();

        assertAll(
                () -> assertThat(searchId).isNotNull().isNotBlank(),
                () -> assertThat(published.searchId()).isEqualTo(searchId),
                () -> assertThat(published.hotelId()).isEqualTo(HOTEL_ID),
                () -> assertThat(published.checkIn()).isEqualTo(CHECK_IN),
                () -> assertThat(published.checkOut()).isEqualTo(CHECK_OUT),
                () -> assertThat(published.ages()).isEqualTo(AGES)
        );
    }

    @Test
    void create_shouldGenerateUniqueSearchIds() {
        String id1 = service.create(HOTEL_ID, CHECK_IN, CHECK_OUT, AGES);
        String id2 = service.create(HOTEL_ID, CHECK_IN, CHECK_OUT, AGES);

        assertThat(id1).isNotEqualTo(id2);
    }
}