package com.search_api.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "SEARCHES")
public class SearchEntity {

    @Id
    @Column(name = "SEARCH_ID", length = 36)
    private String searchId;

    @Column(name = "HOTEL_ID", nullable = false, length = 100)
    private String hotelId;

    @Column(name = "CHECK_IN", nullable = false)
    private LocalDate checkIn;

    @Column(name = "CHECK_OUT", nullable = false)
    private LocalDate checkOut;

    @Column(name = "AGES", nullable = false, length = 500)
    private String ages;

    public SearchEntity() {}

    public SearchEntity(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut, String ages) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages;
    }

    public String getSearchId() { return searchId; }
    public String getHotelId() { return hotelId; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public String getAges() { return ages; }

}
