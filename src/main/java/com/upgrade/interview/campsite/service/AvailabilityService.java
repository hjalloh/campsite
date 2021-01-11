package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.controller.BookingController;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvailabilityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingRepository bookingRepository;

    public AvailabilityService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<AvailabilityDTO> availabilities(LocalDate from, LocalDate to) {
        LocalDate startDate = (from != null) ? from : LocalDate.now();
        LocalDate defaultEndDateRange = LocalDate.now().plusMonths(1);
        LocalDate endDate = (to != null) ? to : defaultEndDateRange;
        if (startDate.isAfter(endDate)) {
            LOGGER.error("Invalid date range: start date {} is greater than end date {}", from, to);
            throw new InvalidInputException("Invalid date range: start date is greater than end date");
        }

        List<BookingEntity> freeDateRanges = bookingRepository.findFreeBookings(startDate, endDate);
        List<AvailabilityDTO> availabilities = freeDateRanges.stream()
                .map(bookingEntity -> new AvailabilityDTO(bookingEntity.getArrivalDate(), bookingEntity.getDepartureDate()))
                .sorted(Comparator.comparing(AvailabilityDTO::getStart))
                .collect(Collectors.toList());
        while (defaultEndDateRange.isBefore(endDate)) {
            availabilities.add(new AvailabilityDTO(defaultEndDateRange, defaultEndDateRange.plusDays(1)));
            defaultEndDateRange = defaultEndDateRange.plusDays(1);
        }

        LOGGER.info("{} free days", availabilities.size());
        return availabilities;
    }

}
