package com.search_api.infrastructure.adapter.out.kafka;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.out.SearchEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SearchKafkaProducer implements SearchEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SearchKafkaProducer.class);

    private final KafkaTemplate<String, Search> kafkaTemplate;
    private final String topic;

    public SearchKafkaProducer(KafkaTemplate<String, Search> kafkaTemplate,
                               @Value("${kafka.topics.hotel-availability-searches}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(Search search) {
        kafkaTemplate.send(topic, search.searchId(), search)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Error al publicar el evento de búsqueda para searchId={}", search.searchId(), ex);
                    } else {
                        log.debug("Evento de búsqueda publicado para searchId={}", search.searchId());
                    }
                });
    }
}