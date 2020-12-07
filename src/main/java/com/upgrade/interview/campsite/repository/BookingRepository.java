package com.upgrade.interview.campsite.repository;

import com.upgrade.interview.campsite.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    @Query(value = "SELECT * FROM booking b WHERE b.status <> ?1 " +
                    "AND (b.arrival_date BETWEEN ?2 AND ?3 " +
                    "   OR b.departure_date BETWEEN ?4 AND ?5 " +
                    "   OR b.arrival_date < ?6 AND b.departure_date > ?7)",
            nativeQuery = true)
    List<BookingEntity> findBookings(String statusToExclude, LocalDate arrivalDateStart, LocalDate arrivalDateEnd,
                                     LocalDate departureDateStart, LocalDate departureDateEnd,
                                     LocalDate from, LocalDate to);

    @Query(value = "SELECT count(*) FROM booking b WHERE b.status <> ?1 " +
            "AND (b.arrival_date BETWEEN ?2 AND ?3 " +
            "   OR b.departure_date BETWEEN ?4 AND ?5 " +
            "   OR b.arrival_date < ?6 AND b.departure_date > ?7)",
            nativeQuery = true)
    long bookingsCount(String statusToExclude, LocalDate arrivalDateStart, LocalDate arrivalDateEnd,
                                     LocalDate departureDateStart, LocalDate departureDateEnd,
                                     LocalDate from, LocalDate to);

}
