package com.search_api.infrastructure.adapter.out.persistence;

import com.search_api.domain.model.Search;
import com.search_api.domain.port.out.SearchRepository;
import com.search_api.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SearchPersistenceAdapter implements SearchRepository {

    private final SearchJpaRepository jpaRepository;

    public SearchPersistenceAdapter(SearchJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(Search search) {
        SearchEntity entity = new SearchEntity(
                search.searchId(),
                search.hotelId(),
                search.checkIn(),
                search.checkOut(),
                search.ages()
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
        return jpaRepository
                .findByHotelIdAndCheckInAndCheckOut(
                        search.hotelId(), search.checkIn(), search.checkOut())
                .stream()
                .filter(e -> e.getAges().equals(search.ages()))
                .count();
    }

    private Search toDomain(SearchEntity entity) {
        return new Search(
                entity.getSearchId(),
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                entity.getAges()
        );
    }

}
