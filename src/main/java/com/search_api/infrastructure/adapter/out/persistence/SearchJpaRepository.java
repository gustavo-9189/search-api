package com.search_api.infrastructure.adapter.out.persistence;

import com.search_api.infrastructure.adapter.out.persistence.entity.SearchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SearchJpaRepository extends JpaRepository<SearchEntity, String> {

    @Query("SELECT e FROM SearchEntity e WHERE e.hotelId = :hotelId AND e.checkIn = :checkIn AND e.checkOut = :checkOut")
    List<SearchEntity> findByHotelIdAndCheckInAndCheckOut(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}