package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.exception.CampsiteAlreadyBookedException;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.mapper.BookingMapper;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.upgrade.interview.campsite.utils.BookingStatus.FREE;
import static com.upgrade.interview.campsite.utils.BookingStatus.RESERVED;

@Service
public class BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final String bookingMaxDays;

    public BookingService(BookingRepository bookingRepository, BookingMapper bookingMapper, @Value("${campsite.reservation.max_days}") String bookingMaxDays) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.bookingMaxDays = bookingMaxDays;
    }

    @Transactional(readOnly = true)
    public Collection<BookingDTO> bookings(LocalDate from, LocalDate to) {
        final LocalDate startDate = (from != null) ? from : LocalDate.now();
        final LocalDate endDate = (to != null) ? to : LocalDate.now().plusMonths(1);
        if (startDate.isAfter(endDate)) {
            LOGGER.error("Invalid date range: start date {} is greater than end date {}", from, to);
            throw new InvalidInputException("Invalid date range: start date is greater than end date");
        }

        return bookingRepository
                .findReservedBookings(startDate, endDate, startDate, endDate, startDate, endDate)
                .stream()
                .map(bookingMapper::entityToDTO)
                .sorted(Comparator.comparing(BookingDTO::getArrivalDate))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long book(final BookingDTO booking) {
        checkBookingDateRange(booking);
        List<BookingEntity> free = this.bookingRepository.findByArrivalDateGreaterThanEqualAndDepartureDateLessThanEqual(booking.getArrivalDate(), booking.getDepartureDate());
        if (free.stream().anyMatch(entity -> RESERVED.name().equalsIgnoreCase(entity.getStatus()))) {
            LOGGER.error("Invalid booking dates: campsite already booked between {} and {}", booking.getArrivalDate(), booking.getDepartureDate());
            throw new CampsiteAlreadyBookedException("Invalid booking dates: campsite already booked between " + booking.getArrivalDate() + " and " + booking.getDepartureDate() + ". Please choose another date range");
        }

        if (free.size() == 1) {
            return singleDayBooking(booking, free);
        }

        return manyDaysBooking(booking, free);
    }



    private Long manyDaysBooking(BookingDTO booking, List<BookingEntity> toBook) {
        final BookingEntity parentBooking = this.bookingRepository.saveAndFlush(this.bookingMapper.dtoToEntity(booking));
        try {
            toBook.forEach(entity -> {
                entity.setStatus(RESERVED.name());
                entity.setVisitorFullName(booking.getVisitorFullName());
                entity.setVisitorEmail(booking.getVisitorEmail());
                entity.setParentId(parentBooking.getId());
            });
            this.bookingRepository.saveAll(toBook);
            parentBooking.setParentId(parentBooking.getId());
            this.bookingRepository.saveAndFlush(parentBooking);
        } catch (ObjectOptimisticLockingFailureException lockingFailureException) {
            this.bookingRepository.deleteById(parentBooking.getId());
            LOGGER.error("Invalid booking dates: campsite already booked between {} and {}", booking.getArrivalDate(), booking.getDepartureDate());
            throw new CampsiteAlreadyBookedException("Invalid booking dates: campsite already booked between " + booking.getArrivalDate() + " and " + booking.getDepartureDate() + ". Please choose another date range");
        }

        return parentBooking.getId();
    }

    private Long singleDayBooking(BookingDTO booking, List<BookingEntity> free) {
        BookingEntity entity = free.get(0);
        try {
            entity.setStatus(RESERVED.name());
            entity.setVisitorFullName(booking.getVisitorFullName());
            entity.setVisitorEmail(booking.getVisitorEmail());
            entity.setParentId(entity.getId());
            this.bookingRepository.saveAndFlush(entity);
            return entity.getId();
        } catch (ObjectOptimisticLockingFailureException lockingFailureException) {
            LOGGER.error("Invalid booking dates: campsite already booked between {} and {}", booking.getArrivalDate(), booking.getDepartureDate());
            throw new CampsiteAlreadyBookedException("Invalid booking dates: campsite already booked between " + booking.getArrivalDate() + " and " + booking.getDepartureDate() + ". Please choose another date range");
        }
    }

    @Transactional
    public Long modify(final Long bookingUID, final BookingDTO bookingDTO) {
        Optional<BookingEntity> bookingEntity = this.bookingRepository.findById(bookingUID);
        bookingEntity.orElseThrow(() -> new InvalidInputException("Invalid booking ID: no booking found from ID " + bookingUID));
        Long newBookingUID = this.book(bookingDTO);
        this.cancel(bookingUID);
        return newBookingUID;
    }

    @Transactional
    public void cancel(final Long bookingUID) {
        List<BookingEntity> bookingsToCancel = this.bookingRepository.findByIdOrParentId(bookingUID, bookingUID);
        if (bookingsToCancel.isEmpty()) {
            LOGGER.error("Invalid booking ID: no booking found from ID {}", bookingUID);
            throw new InvalidInputException("Invalid booking ID: no booking found from ID " + bookingUID);
        }

        if (bookingsToCancel.size() == 1) {
            BookingEntity booking = bookingsToCancel.get(0);
            logicalCancel(booking);
        } else {
            bookingsToCancel.forEach(this::cancelHelper);
        }
    }

    private void cancelHelper(BookingEntity booking) {
        if (booking.getId().equals(booking.getParentId())) {
            this.bookingRepository.deleteById(booking.getId());
        } else {
            logicalCancel(booking);
        }
    }

    private void logicalCancel(BookingEntity booking) {
        booking.setStatus(FREE.name());
        booking.setParentId(null);
        booking.setVisitorEmail(null);
        booking.setVisitorFullName(null);
        this.bookingRepository.saveAndFlush(booking);
    }

    private void checkBookingDateRange(final BookingDTO booking) {
        if (LocalDate.now().isAfter(booking.getArrivalDate()) || LocalDate.now().equals(booking.getArrivalDate())) {
            LOGGER.error("Invalid arrival date {}: campsite has to be reserved minimum 1 day ahead", booking.getArrivalDate());
            throw new InvalidInputException("Invalid arrival date: campsite has to be reserved minimum 1 day ahead");
        }

        if (booking.getArrivalDate().isAfter(LocalDate.now().plusMonths(1))) {
            LOGGER.error("Invalid arrival date {}: campsite cannot be reserved more than 1 month ahead", booking.getArrivalDate());
            throw new InvalidInputException("Invalid arrival date: campsite cannot be reserved more than 1 month ahead");
        }

        if (booking.getArrivalDate().isEqual(booking.getDepartureDate()) || booking.getArrivalDate().isAfter(booking.getDepartureDate())) {
            LOGGER.error("Invalid booking date range: arrival date {} is equal / greater than departure date {}", booking.getArrivalDate(), booking.getDepartureDate());
            throw new InvalidInputException("Invalid booking date range: arrival date is equal/greater than departure date");
        }

        if (ChronoUnit.DAYS.between(booking.getArrivalDate(), booking.getDepartureDate()) > Integer.parseInt(this.bookingMaxDays)) {
            LOGGER.error("Invalid booking dates: the campsite cannot be booked for more than {} days. ArrivalDate={}, DepartureDate={}", bookingMaxDays, booking.getArrivalDate(), booking.getDepartureDate());
            throw new InvalidInputException("Invalid booking dates: the campsite cannot be booked for more than " + bookingMaxDays + " days in a row");
        }
    }
}
