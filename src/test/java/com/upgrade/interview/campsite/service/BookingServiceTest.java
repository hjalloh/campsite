package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.mapper.BookingMapper;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.exception.CampsiteAlreadyBookedException;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingServiceTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @SpyBean
    private BookingService bookingService;

    @Test
    public void testConcurrentBook_should_succeed() throws InterruptedException {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().plusWeeks(3);
        LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking_1 = booking(arrivalDate, departureDate);
        final BookingDTO booking_2 = booking(arrivalDate.plusDays(2), departureDate.plusDays(1));

        Callable<Long> booking_1_callable = () -> bookingService.book(booking_1);
        Callable<Long> booking_2_callable = () -> bookingService.book(booking_2);

        List<Callable<Long>> callables = new ArrayList<>();
        callables.add(booking_1_callable);
        callables.add(booking_2_callable);

        // WHEN
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        threadPool.invokeAll(callables);
        threadPool.shutdown();
        threadPool.awaitTermination(1, TimeUnit.MINUTES);

        // THEN
        Collection<BookingDTO> bookings = bookingService.bookings(arrivalDate, departureDate);
        assertAll(
                () -> assertEquals(1, bookings.size()),
                () -> Mockito.verify(bookingService, Mockito.times(2)).book(Mockito.any())
        );
    }

    @Test
    public void testNonConcurrentBook_should_succeed() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().plusWeeks(5);
        LocalDate departureDate = arrivalDate.plusDays(3);
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
        LocalDate arrivalDate = LocalDate.now().plusWeeks(6);
        LocalDate departureDate = arrivalDate.plusDays(3);
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
        LocalDate arrivalDate = LocalDate.now().plusWeeks(10);
        LocalDate departureDate = arrivalDate.plusDays(3);
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
        LocalDate arrivalDate = LocalDate.now().plusWeeks(8);
        LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking = booking(arrivalDate, departureDate);

        // WHEN
        bookingService.book(booking);
        CampsiteAlreadyBookedException exception = assertThrows(CampsiteAlreadyBookedException.class, () -> bookingService.book(booking));
        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    public void testNonConcurrentBook_should_throw_exception_when_booking_period_exceed_3_days() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().plusWeeks(11);
        LocalDate departureDate = arrivalDate.plusDays(4);
        final BookingDTO booking = booking(arrivalDate, departureDate);

        // WHEN
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.book(booking));
        assertTrue(exception.getMessage().contains("for more than 3 days"));
    }


    @Test
    public void testNonConcurrentBook_should_throw_exception_when_departure_date_is_before_arrival_date() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().plusWeeks(12);
        LocalDate departureDate = arrivalDate.minusDays(1);
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
    public void testBook_cancel_then_book_should_succeed() {
        LocalDate arrivalDate = LocalDate.now().minusWeeks(15);
        LocalDate departureDate = arrivalDate.plusDays(3);
        BookingDTO booking = booking(arrivalDate, departureDate);
        Long bookingUID_1 = bookingService.book(booking);
        bookingService.cancel(bookingUID_1);

        // WHEN
        Long bookingUID_2 = bookingService.book(booking);
        Optional<BookingEntity> entity = bookingRepository.findById(bookingUID_2);

        // THEN
        assertAll(
                () -> assertNotEquals(bookingUID_1, bookingUID_2),
                () -> assertTrue(entity.isPresent()),
                () -> assertEquals(arrivalDate, entity.get().getArrivalDate()),
                () -> assertEquals(departureDate, entity.get().getDepartureDate())
        );
    }

    @Test
    public void testCancel_should_succeed() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().minusWeeks(6);
        LocalDate departureDate = arrivalDate.plusDays(3);
        final BookingDTO booking_1 = booking(arrivalDate, departureDate);
        LocalDate departureDateBooking_2 = departureDate.plusDays(3);
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

    @Test
    public void testModify_should_throw_exception_when_booking_is_nonexistent() {
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.modify(Long.MAX_VALUE, null));
        assertTrue(exception.getMessage().contains("no booking found from ID"));
    }

    @Test
    public void testModify_should_throw_exception_when_booking_period_exceed_3_days() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().minusWeeks(11);
        LocalDate departureDate = arrivalDate.plusDays(3);
        BookingDTO booking = booking(arrivalDate, departureDate);
        Long bookingUID = bookingService.book(booking);
        booking.setDepartureDate(departureDate.plusDays(6));

        // WHEN
        InvalidInputException exception = assertThrows(InvalidInputException.class, () -> bookingService.modify(bookingUID, booking));
        assertTrue(exception.getMessage().contains("for more than 3 days"));
    }

    @Test
    public void testModify_should_succeed() {
        // GIVEN
        LocalDate arrivalDate = LocalDate.now().minusWeeks(15);
        LocalDate departureDate = arrivalDate.plusDays(3);
        BookingDTO booking = booking(arrivalDate, departureDate);
        Long bookingUID = bookingService.book(booking);
        LocalDate newArrivalDate = arrivalDate.plusDays(6);
        booking.setArrivalDate(newArrivalDate);
        LocalDate newDepartureDate = newArrivalDate.plusDays(2);
        booking.setDepartureDate(newDepartureDate);

        // WHEN
        bookingService.modify(bookingUID, booking);
        Optional<BookingEntity> entity = bookingRepository.findById(bookingUID);

        // THEN
        assertAll(
                () -> assertTrue(entity.isPresent()),
                () -> assertEquals(newArrivalDate, entity.get().getArrivalDate()),
                () -> assertEquals(newDepartureDate, entity.get().getDepartureDate()),
                () -> assertEquals("hamidou.diallo@upgrade.com", entity.get().getVisitorEmail())
        );
    }

    private BookingDTO booking(LocalDate arrivalDate, LocalDate departureDate) {
        return new BookingDTO(null, "hamidou.diallo@upgrade.com", "Hamidou Diallo", arrivalDate, departureDate);
    }
}