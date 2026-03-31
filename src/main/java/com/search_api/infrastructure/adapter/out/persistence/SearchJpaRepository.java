package com.search_api.infrastructure.adapter.out.persistence;

import com.search_api.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SearchJpaRepository extends JpaRepository<SearchEntity, String> {

    @Query("SELECT COUNT(s) FROM SearchEntity s WHERE s.hotelId = :hotelId AND s.checkIn = :checkIn AND s.checkOut = :checkOut AND s.ages = :ages")
    long countBySearchFields(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("ages") String ages
    );
}