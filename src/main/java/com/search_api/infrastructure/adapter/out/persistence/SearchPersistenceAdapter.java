package com.search_api.infrastructure.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search_api.domain.model.Search;
import com.search_api.domain.port.out.SearchRepository;
import com.search_api.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SearchPersistenceAdapter implements SearchRepository {

    private final SearchJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    public SearchPersistenceAdapter(SearchJpaRepository jpaRepository, ObjectMapper objectMapper) {
        this.jpaRepository = jpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Search search) {
        SearchEntity entity = new SearchEntity(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                serializeAges(search.ages())
        );
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Search> findBySearchId(String searchId) {
        return jpaRepository.findById(searchId)
                .map(this::toDomain);
    }

    @Override
    public long countBySearchFields(Search search) {
        return jpaRepository.countBySearchFields(
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                serializeAges(search.ages())
        );
    }

    private Search toDomain(SearchEntity entity) {
        return new Search(
                entity.getSearchId(),
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                deserializeAges(entity.getAges())
        );
    }

    private String serializeAges(List<Integer> ages) {
        try {
            return objectMapper.writeValueAsString(ages);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error al serializar el campo ages", e);
        }
    }

    private List<Integer> deserializeAges(String ages) {
        try {
            return objectMapper.readValue(ages, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error al deserializar el campo ages", e);
        }
    }

}
