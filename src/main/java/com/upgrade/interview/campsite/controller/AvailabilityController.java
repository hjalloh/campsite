package com.upgrade.interview.campsite.controller;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.exception.InvalidInputException;
import com.upgrade.interview.campsite.service.AvailabilityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.Collection;


@RestController
public class AvailabilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AvailabilityController.class);

    @Autowired
    private AvailabilityService availabilityService;

    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Availabilities if there is one"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Request processing error"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Invalid input")
    })
    @ApiOperation(value = "Availabilities for a given date range with the default being 1 month")
    @GetMapping("/availabilities")
    public Collection<AvailabilityDTO> availabilities(
            @ApiParam(value = "Start date range", name = "from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "End date range", name = "to")
            @RequestParam(required = false) LocalDate to) {
        LOGGER.info("About to get availabilities");
        final LocalDate startDate = (from != null) ? from : LocalDate.now();
        final LocalDate endDate = (to != null) ? to : LocalDate.now().plusMonths(1).minusDays(1);
        if (startDate.isAfter(endDate)) {
            LOGGER.error("Invalid date range: start date {} is greater than end date {}", from, to);
            throw new InvalidInputException("Invalid date range: start date is greater than end date");
        }

        return this.availabilityService.availabilities(startDate, endDate);
    }
}
