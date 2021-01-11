package com.upgrade.interview.campsite.repository;

import com.upgrade.interview.campsite.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {


    @Query(value = "SELECT * FROM booking b " +
            "   WHERE b.status = 'RESERVED' " +
            "   AND b.parent_id = b.id " +
            "   AND (b.arrival_date BETWEEN ?1 AND ?2 " +
            "   OR b.departure_date BETWEEN ?3 AND ?4 " +
            "   OR b.arrival_date < ?5 AND b.departure_date > ?6)",
            nativeQuery = true)
    List<BookingEntity> findReservedBookings(LocalDate arrivalDateStart, LocalDate arrivalDateEnd,
                                             LocalDate departureDateStart, LocalDate departureDateEnd,
                                             LocalDate from, LocalDate to);


    @Query(value = "SELECT * FROM booking b " +
            "   WHERE b.status = 'FREE' " +
            "   AND b.arrival_date >= ?1 " +
            "   AND b.departure_date <= ?2",
            nativeQuery = true)
    List<BookingEntity> findFreeBookings(LocalDate arrivalDate, LocalDate departureDate);

    List<BookingEntity> findByArrivalDateGreaterThanEqualAndDepartureDateLessThanEqual(@Param("arrival_date") LocalDate arrivalDate,
                                                                                               @Param("departure_date") LocalDate departureDate);

    List<BookingEntity> findByIdOrParentId(@Param("id") Long id, @Param("parent_id") Long parentId);


}
