package com.search_api.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "SEARCH_AGES", joinColumns = @JoinColumn(name = "SEARCH_ID"))
    @Column(name = "AGE", nullable = false)
    @OrderColumn(name = "AGE_ORDER")
    private List<Integer> ages;

    public SearchEntity() {}

    public SearchEntity(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {
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
    public List<Integer> getAges() { return ages; }

}