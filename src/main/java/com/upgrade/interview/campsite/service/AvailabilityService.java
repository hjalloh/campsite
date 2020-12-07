package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.controller.BookingController;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.repository.BookingRepository;
import com.upgrade.interview.campsite.utils.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class AvailabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional(readOnly = true)
    public List<AvailabilityDTO> availabilities(LocalDate from, LocalDate to) {
        LocalDate startDate = (from != null) ? from : LocalDate.now();
        LocalDate endDate = (to != null) ? to : LocalDate.now().plusMonths(1).minusDays(1);
        if (startDate.isAfter(endDate)) {
            LOGGER.error("Invalid date range: start date {} is greater than end date {}", from, to);
            throw new InvalidInputException("Invalid date range: start date is greater than end date");
        }

        List<BookingEntity> bookings = bookingRepository.findBookings(BookingStatus.CANCELLED.name(), startDate, endDate, startDate, endDate, startDate, endDate);
        if (bookings.isEmpty()) {
            return Collections.singletonList(new AvailabilityDTO(startDate, endDate));
        }

        List<AvailabilityDTO> result = new ArrayList<>();
        if (bookings.size() == 1) {
            BookingEntity booking = bookings.get(0);
            Optional<AvailabilityDTO> availability = build(booking.getArrivalDate(), startDate, startDate, booking.getArrivalDate());
            availability.ifPresent(result::add);
            availability = build(endDate, booking.getDepartureDate(), booking.getDepartureDate(), endDate);
            availability.ifPresent(result::add);
            return result;
        }

        bookings.sort(Comparator.comparing(BookingEntity::getArrivalDate));
        BookingEntity booking = bookings.get(0);
        Optional<AvailabilityDTO> availability = build(booking.getArrivalDate(), startDate, startDate, booking.getArrivalDate());
        availability.ifPresent(result::add);

        ListIterator<BookingEntity> currentIterator = bookings.listIterator();
        ListIterator<BookingEntity> nextIterator = bookings.listIterator(1);
        while (nextIterator.hasNext()) {
            BookingEntity current = currentIterator.next();
            BookingEntity next = nextIterator.next();
            availability = build(next.getArrivalDate(), current.getDepartureDate(), current.getDepartureDate(), next.getArrivalDate());
            availability.ifPresent(result::add);
        }

        BookingEntity lastBooking = currentIterator.next();
        availability = build(endDate, lastBooking.getDepartureDate(), lastBooking.getDepartureDate(), endDate);
        availability.ifPresent(result::add);

        return result;
    }

    private Optional<AvailabilityDTO> build(LocalDate nextBookingArrivalDate, LocalDate currentBookingDepartureDate, LocalDate availabilityStart, LocalDate availabilityEnd) {
        if (currentBookingDepartureDate.isBefore(nextBookingArrivalDate)) {
            return Optional.of(new AvailabilityDTO(availabilityStart, availabilityEnd));
        }
        return Optional.empty();
    }
}
