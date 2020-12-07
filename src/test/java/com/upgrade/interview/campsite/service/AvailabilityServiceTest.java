package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class AvailabilityServiceTest {

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;


    @Test
    public void testAvailabilities_for_default_date_range_when_no_bookings_should_whole_period_free() {
        List<AvailabilityDTO> availabilities = availabilityService.availabilities(null, null);
        assertAll(
                () -> assertEquals(1, availabilities.size()),
                () -> assertEquals(LocalDate.now(), availabilities.get(0).getStart()),
                () -> assertEquals(LocalDate.now().plusMonths(1).minusDays(1), availabilities.get(0).getEnd())
        );
    }

    @Test
    public void testAvailabilities_when_there_is_1_booking_should_have_2_buckets_date_range_free() {
        // GIVEN
        LocalDate start = LocalDate.now().plusMonths(10);
        LocalDate end = start.plusMonths(1).minusDays(1);
        LocalDate arrivalDate = start.plusDays(5);
        LocalDate departureDate = arrivalDate.plusDays(3);
        BookingDTO booking = this.booking(arrivalDate, departureDate);
        bookingService.book(booking);

        // WHEN
        List<AvailabilityDTO> availabilities = availabilityService.availabilities(start, end);

        // THEN
        assertAll(
                () -> assertEquals(2, availabilities.size()),
                () -> assertEquals(start, availabilities.get(0).getStart()),
                () -> assertEquals(arrivalDate, availabilities.get(0).getEnd()),
                () -> assertEquals(departureDate, availabilities.get(1).getStart()),
                () -> assertEquals(end, availabilities.get(1).getEnd())
        );
    }

    private BookingDTO booking(LocalDate arrivalDate, LocalDate departureDate) {
        return new BookingDTO(null, "hamidou.diallo@upgrade.com", "Hamidou Diallo", arrivalDate, departureDate);
    }
}