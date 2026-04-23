package com.search_api.infrastructure.adapter.in.kafka;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.in.SaveSearchUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(SearchKafkaConsumer.class);

    private final SaveSearchUseCase saveSearchUseCase;

    public SearchKafkaConsumer(SaveSearchUseCase saveSearchUseCase) {
        this.saveSearchUseCase = saveSearchUseCase;
    }

    /**
     * Consume eventos de búsqueda desde Kafka y los persiste en base de datos.
     * El listener se ejecuta en virtual threads (configurado en KafkaConfig).
     */
    @KafkaListener(
            topics = "${kafka.topics.hotel-availability-searches}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(Search search) {
        log.debug("Evento de búsqueda recibido para searchId={}", search.searchId());
        try {
            saveSearchUseCase.save(search);
            log.debug("Búsqueda persistida con searchId={}", search.searchId());
        } catch (Exception e) {
            log.error("Error al persistir la búsqueda con searchId={}", search.searchId(), e);
            throw e;
        }
    }

}
