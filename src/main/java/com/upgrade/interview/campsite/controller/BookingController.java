package com.upgrade.interview.campsite.controller;

import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.service.BookingService;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;

@RestController
public class BookingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @ApiOperation(value = "To retrieve the bookings for a given date range with the default being 1 month")
    @GetMapping("/bookings")
    public Collection<BookingDTO> bookings(
            @ApiParam(value = "Start date range", name = "from", example = "YYYY-MM-DD")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "End date range", name = "to", example = "YYYY-MM-DD")
            @RequestParam(required = false) LocalDate to) {
        LOGGER.info("About to get all the bookings");
        final LocalDate startDate = (from != null) ? from : LocalDate.now();
        final LocalDate endDate = (to != null) ? to : LocalDate.now().plusMonths(1).minusDays(1);
        if (startDate.isAfter(endDate)) {
            LOGGER.error("Invalid date range: start date {} is greater than end date {}", from, to);
            throw new InvalidInputException("Invalid date range: start date is greater than end date");
        }
        return this.bookingService.bookings(startDate, endDate);
    }

   // public void book()

}
