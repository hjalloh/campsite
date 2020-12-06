package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.BookingMapper;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.exception.CampsiteAlreadyBookedException;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private BookingService bookingService;

    @Test
    public void testNonConcurrentBook_should_succeed() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().plusWeeks(5);
        final LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking = booking(arrivalDate, departureDate);

        // WHEN
        final Long bookUID = bookingService.book(booking);
        final Optional<BookingEntity> entity = this.bookingRepository.findById(bookUID);

        // THEN
        assertAll(
                () -> assertNotNull(bookUID),
                () -> assertTrue(entity.isPresent()),
                () -> assertEquals(arrivalDate, entity.get().getArrivalDate()),
                () -> assertEquals(departureDate, entity.get().getDepartureDate())
        );
    }

    @Test
    public void testNonConcurrentBook_should_succeed_when_arrival_date_equal_to_departure_date_of_another_booking() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().plusWeeks(6);
        final LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking_1 = booking(arrivalDate, departureDate);
        final BookingDTO booking_2 = booking(departureDate, departureDate.plusDays(3));

        // WHEN
        bookingService.book(booking_1);
        final Long bookUID = bookingService.book(booking_2);
        final Optional<BookingEntity> entity = this.bookingRepository.findById(bookUID);

        // THEN
        assertAll(
                () -> assertNotNull(bookUID),
                () -> assertTrue(entity.isPresent()),
                () -> assertEquals(departureDate, entity.get().getArrivalDate()),
                () -> assertEquals(departureDate.plusDays(3), entity.get().getDepartureDate())
        );
    }

    @Test
    public void testNonConcurrentBook_should_succeed_when_departure_date_equal_to_arrival_date_of_another_booking() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().plusWeeks(10);
        final LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking_1 = booking(arrivalDate, departureDate);
        final BookingDTO booking_2 = booking(arrivalDate.minusDays(3), arrivalDate);

        // WHEN
        bookingService.book(booking_1);
        final Long bookUID = bookingService.book(booking_2);
        final Optional<BookingEntity> entity = this.bookingRepository.findById(bookUID);

        // THEN
        assertAll(
                () -> assertNotNull(bookUID),
                () -> assertTrue(entity.isPresent()),
                () -> assertEquals(arrivalDate.minusDays(3), entity.get().getArrivalDate()),
                () -> assertEquals(arrivalDate, entity.get().getDepartureDate())
        );
    }

    @Test
    public void testNonConcurrentBook_should_throw_exception_when_date_range_already_booked() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().plusWeeks(8);
        final LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking = booking(arrivalDate, departureDate);

        // WHEN
        bookingService.book(booking);
        CampsiteAlreadyBookedException exception = assertThrows(CampsiteAlreadyBookedException.class, () -> bookingService.book(booking));
        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    public void testNonConcurrentBook_should_throw_exception_when_booking_period_exceed_3_days() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().plusWeeks(11);
        final LocalDate departureDate = arrivalDate.plusDays(4);
        final BookingDTO booking = booking(arrivalDate, departureDate);

        // WHEN
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.book(booking));
        assertTrue(exception.getMessage().contains("for more than 3 days"));
    }


    @Test
    public void testNonConcurrentBook_should_throw_exception_when_departure_date_is_before_arrival_date() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().plusWeeks(12);
        final LocalDate departureDate = arrivalDate.minusDays(1);
        final BookingDTO booking = booking(arrivalDate, departureDate);

        // WHEN
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.book(booking));
        assertTrue(exception.getMessage().contains("is equal/greater than departure date"));
    }

    @Test
    public void testCancel_should_throw_exception_when_booking_is_nonexistent() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.cancel(Long.MAX_VALUE));
        assertTrue(exception.getMessage().contains("no booking found from ID"));
    }

    @Test
    public void testCancel_should_succeed() {
        // GIVEN
        final LocalDate arrivalDate = LocalDate.now().minusWeeks(6);
        final LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking_1 = booking(arrivalDate, departureDate);
        final LocalDate departureDateBooking_2 = departureDate.plusDays(3);
        final BookingDTO booking_2 = booking(departureDate, departureDateBooking_2);

        // WHEN
        final Long booking_1_uid = bookingService.book(booking_1);
        final Long booking_2_uid = bookingService.book(booking_2);
        final Collection<BookingDTO> bookingsBeforeCancellation = bookingService.bookings(arrivalDate, departureDateBooking_2);
        bookingService.cancel(booking_1_uid);
        final Collection<BookingDTO> bookingsAfterCancellation = bookingService.bookings(arrivalDate, departureDateBooking_2);

        assertAll(
                () -> assertNotNull(booking_1_uid),
                () -> assertNotNull(booking_2_uid),
                () -> assertEquals(2, bookingsBeforeCancellation.size()),
                () -> assertEquals(1, bookingsAfterCancellation.size())
        );
    }

    private BookingDTO booking(LocalDate arrivalDate, LocalDate departureDate) {
        return new BookingDTO(null, "hamidou.diallo@upgrade.com", "Hamidou Diallo", arrivalDate, departureDate);
    }
}