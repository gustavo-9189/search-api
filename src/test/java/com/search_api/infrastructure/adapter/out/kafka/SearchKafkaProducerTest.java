package com.search_api.infrastructure.adapter.out.kafka;

import com.search_api.domain.model.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchKafkaProducerTest {

    private static final String TOPIC = "hotel_availability_searches";
    private static final Search SEARCH = new Search(
            "id-1", "hotel1",
            LocalDate.of(2023, 12, 29),
            LocalDate.of(2023, 12, 31),
            List.of(30, 29)
    );

    @Mock
    private KafkaTemplate<String, Search> kafkaTemplate;

    private SearchKafkaProducer producer;

    @BeforeEach
    void setUp() {
        producer = new SearchKafkaProducer(kafkaTemplate, TOPIC);
    }

    @Test
    void publish_shouldSendMessageToKafka() {
        CompletableFuture<SendResult<String, Search>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));
        when(kafkaTemplate.send(TOPIC, SEARCH.searchId(), SEARCH)).thenReturn(future);

        producer.publish(SEARCH);

        verify(kafkaTemplate).send(TOPIC, SEARCH.searchId(), SEARCH);
    }

    @Test
    void publish_shouldHandleKafkaFailureGracefully() {
        CompletableFuture<SendResult<String, Search>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Error de Kafka"));
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(future);

        // Should not throw — failure is logged, not propagated
        producer.publish(SEARCH);

        verify(kafkaTemplate).send(TOPIC, SEARCH.searchId(), SEARCH);
    }
}