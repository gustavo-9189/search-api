package com.search_api.infrastructure.adapter.in.kafka;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.in.SaveSearchUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchKafkaConsumerTest {

    private static final Search SEARCH = new Search(
            "id-1", "hotel1",
            LocalDate.of(2023, 12, 29),
            LocalDate.of(2023, 12, 31),
            List.of(30, 29)
    );

    @Mock
    private SaveSearchUseCase saveSearchUseCase;

    private SearchKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new SearchKafkaConsumer(saveSearchUseCase);
    }

    @Test
    void consume_shouldSaveSearch() {
        consumer.consume(SEARCH);

        verify(saveSearchUseCase).save(SEARCH);
    }

    @Test
    void consume_shouldRethrowExceptionOnSaveFailure() {
        doThrow(new RuntimeException("Error de base de datos")).when(saveSearchUseCase).save(SEARCH);

        assertThrows(RuntimeException.class, () -> consumer.consume(SEARCH));
        verify(saveSearchUseCase).save(SEARCH);
    }

}