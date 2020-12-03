package com.upgrade.interview.campsite.controller;

import com.upgrade.interview.campsite.service.BookingService;
import com.upgrade.interview.campsite.DTO.BookingDTO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class BookingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @ApiOperation(value = "To retrieve all the bookings")
    @GetMapping("/bookings")
    public Collection<BookingDTO> bookings() {
        LOGGER.info("About to get all the bookings");
        return this.bookingService.allBookings();
    }
}
