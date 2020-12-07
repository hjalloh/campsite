package com.upgrade.interview.campsite.controller;

import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.service.BookingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    @ApiOperation(value = "To retrieve the bookings for a given date range with the default being 1 month")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "The bookings list"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Invalid date range: start date is greater than the end date"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Request processing error")
    })
    @GetMapping
    public Collection<BookingDTO> bookings(
            @ApiParam(name = "from", value = "Start date range", example = "YYYY-MM-DD")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(name = "to", value = "End date range", example = "YYYY-MM-DD")
            @RequestParam(required = false) LocalDate to) {
        LOGGER.info("About to get all the bookings");
        return this.bookingService.bookings(from, to);
    }

    @ApiOperation(value = "To reserve the campsite. Return a unique booking identifier")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_CREATED, message = "Campsite successfully booked. Return the booking UUID."),
            @ApiResponse(code = HttpServletResponse.SC_CONFLICT, message = "Campsite already booked at this period. Please select another date range"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Invalid booking date range: either the reservation is for more than 3 days or arrival date is same/greater than the departure date"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Request processing error")
    })
    @PostMapping
    public Long book(@ApiParam(value = "Booking to create", required = true) BookingDTO booking) {
        LOGGER.info("About to book the campsite Booking={}", booking);
        Long bookUID = bookingService.book(booking);
        LOGGER.info("Campsite successfully booked. BookUID={}", bookUID);
        return bookUID;
    }


    @ApiOperation(value = "To cancel a reservation")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Reservation successfully cancelled"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Request processing error")
    })
    @DeleteMapping("/{bookUID}")
    public void cancel(@ApiParam(value = "Unique booking identifier", required = true)
                           @PathVariable("bookUID") Long bookUID) {
        LOGGER.info("About to cancel the booking with ID={}", bookUID);
        this.bookingService.cancel(bookUID);
        LOGGER.info("Booking with ID={} successfully cancelled", bookUID);
    }

    @ApiOperation(value = "To modify a reservation")
    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Reservation successfully modified"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Invalid booking date range: either the reservation is for more than 3 days or arrival date is same/greater than the departure date"),
            @ApiResponse(code = HttpServletResponse.SC_CONFLICT, message = "Campsite already booked at this period. Please select another date range"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Request processing error")
    })
    @PutMapping("/{bookingUID}")
    public void modify(@ApiParam(value = "Unique booking identifier", required = true) @PathVariable("bookingUID") Long bookingUID,
                       @RequestBody BookingDTO booking) {
        LOGGER.info("About to modify the booking with ID={}", bookingUID);
        this.bookingService.modify(bookingUID, booking);
        LOGGER.info("Booking successfully modified");
    }
}
