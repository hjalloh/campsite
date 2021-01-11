package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AvailabilityServiceTest {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;


    @Test
    @Order(1)
    public void testAvailabilities_for_default_date_range_when_no_bookings_should_whole_period_free() {
        List<AvailabilityDTO> availabilities = availabilityService.availabilities(null, null);
        final long days = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().plusMonths(1));
        assertAll(
                () -> assertEquals(days, availabilities.size()),
                () -> assertEquals(LocalDate.now(), availabilities.get(0).getStart()),
                () -> assertEquals(LocalDate.now().plusMonths(1), availabilities.get(availabilities.size() - 1).getEnd())
        );
    }

    @Test
    @Order(2)
    public void testAvailabilities_when_there_is_1_booking_should_have_2_buckets_date_range_free() {
        // GIVEN
        LocalDate now = LocalDate.now();
        final long days = ChronoUnit.DAYS.between(now, now.plusMonths(1)) - 1;
        BookingDTO booking = this.booking(now.plusDays(1), now.plusDays(2));
        bookingService.book(booking);

        // WHEN
        List<AvailabilityDTO> availabilities = availabilityService.availabilities(null, null);

        // THEN
        assertAll(
                () -> assertEquals(days, availabilities.size()),
                () -> assertEquals(now, availabilities.get(0).getStart()),
                () -> assertEquals(LocalDate.now().plusMonths(1), availabilities.get(availabilities.size() - 1).getEnd())
        );
    }

    private BookingDTO booking(LocalDate arrivalDate, LocalDate departureDate) {
        return new BookingDTO(null, "hamidou.diallo@upgrade.com", "Hamidou Diallo", arrivalDate, departureDate);
    }
}