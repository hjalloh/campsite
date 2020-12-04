package com.upgrade.interview.campsite.repository;

import com.upgrade.interview.campsite.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByArrivalDateBetweenOrDepartureDateBetweenOrArrivalDateLessThanAndDepartureDateGreaterThan(
            LocalDate arrivalDateStart, LocalDate arrivalDateEnd,
            LocalDate departureDateStart, LocalDate departureDateEnd,
            LocalDate from, LocalDate to);
}
