package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.BookingMapper;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.exception.CampsiteAlreadyBookedException;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.repository.BookingRepository;
import com.upgrade.interview.campsite.utils.BookingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingService.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingMapper bookingMapper;

    @Value("${campsite.reservation.max_days}")
    private String bookingMaxDays;

    @Transactional(readOnly = true)
    public Collection<BookingDTO> bookings(LocalDate from, LocalDate to) {
        final LocalDate startDate = (from != null) ? from : LocalDate.now();
        final LocalDate endDate = (to != null) ? to : LocalDate.now().plusMonths(1).minusDays(1);
        if (startDate.isAfter(endDate)) {
            LOGGER.error("Invalid date range: start date {} is greater than end date {}", from, to);
            throw new InvalidInputException("Invalid date range: start date is greater than end date");
        }

        return bookingRepository
                .findBookings(BookingStatus.CANCELLED.name(), startDate, endDate, startDate, endDate, startDate, endDate)
                .stream()
                .map(bookingMapper::entityToDTO)
                .sorted(Comparator.comparing(BookingDTO::getArrivalDate))
                .collect(Collectors.toList());
    }

    public Long book(final BookingDTO booking) {
        checkBookingDateRange(booking);
        return this.bookHelper(booking);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Lock(LockModeType.PESSIMISTIC_WRITE) // H2 in-memory only supports this mode of pessimistic locking
    private Long bookHelper(final BookingDTO booking) {
        checkIfRangeDateIsFree(booking);
        return this.bookingRepository.saveAndFlush(bookingMapper.dtoToEntity(booking)).getId();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void cancel(final Long bookingUID) {
        Optional<BookingEntity> booking = this.bookingRepository.findById(bookingUID);
        BookingEntity entity = booking.orElseThrow(() -> new InvalidInputException("Invalid booking ID: no booking found from ID " + bookingUID));
        entity.setStatus(BookingStatus.CANCELLED.name());
        this.bookingRepository.saveAndFlush(entity);
    }

    public void modify(final Long bookingUID, final BookingDTO bookingDTO) {
        Optional<BookingEntity> bookingEntity = this.bookingRepository.findById(bookingUID);
        BookingEntity entity = bookingEntity.orElseThrow(() -> new InvalidInputException("Invalid booking ID: no booking found from ID " + bookingUID));
        checkBookingDateRange(bookingDTO);
        this.modifyHelper(bookingDTO, entity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Lock(LockModeType.PESSIMISTIC_WRITE) // H2 in-memory only supports this mode of pessimistic locking
    private void modifyHelper(final BookingDTO dto, final BookingEntity entity) {
        checkIfRangeDateIsFree(dto);
        entity.setArrivalDate(dto.getArrivalDate());
        entity.setDepartureDate(dto.getDepartureDate());
        entity.setVisitorEmail(dto.getVisitorEmail());
        entity.setVisitorFullName(dto.getVisitorFullName());
        this.bookingRepository.saveAndFlush(entity);
    }

    private void checkIfRangeDateIsFree(BookingDTO dto) {
        List<BookingEntity> currentBookings = bookingRepository.findBookings(BookingStatus.CANCELLED.name(),
                dto.getArrivalDate(), dto.getDepartureDate().minusDays(1),
                dto.getArrivalDate().plusDays(1), dto.getDepartureDate(),
                dto.getArrivalDate(), dto.getDepartureDate());
        if (!currentBookings.isEmpty()) {
            LOGGER.error("Invalid booking dates: campsite already booked between {} and {}", dto.getArrivalDate(), dto.getDepartureDate());
            throw new CampsiteAlreadyBookedException("Invalid booking dates: campsite already booked between " + dto.getArrivalDate() + " and " + dto.getDepartureDate() + ". Please choose another date range");
        }
    }

    private void checkBookingDateRange(final BookingDTO booking) {
        if (booking.getArrivalDate().isEqual(booking.getDepartureDate()) || booking.getArrivalDate().isAfter(booking.getDepartureDate())) {
            LOGGER.error("Invalid booking date range: arrival date {} is equal / greater than departure date {}", booking.getArrivalDate(), booking.getDepartureDate());
            throw new InvalidInputException("Invalid booking date range: arrival date is equal/greater than departure date");
        }

        if (booking.getDepartureDate().compareTo(booking.getArrivalDate()) > Integer.parseInt(this.bookingMaxDays)) {
            LOGGER.error("Invalid booking dates: the campsite cannot be booked for more than {} days. ArrivalDate={}, DepartureDate={}", bookingMaxDays, booking.getArrivalDate(), booking.getDepartureDate());
            throw new InvalidInputException("Invalid booking dates: the campsite cannot be booked for more than " + bookingMaxDays + " days in a row");
        }
    }
}
