package com.search_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "hotel_availability_searches")
@ActiveProfiles("test")
class SearchApiApplicationTests {

    @Test
    void contextLoads() {
    }
}