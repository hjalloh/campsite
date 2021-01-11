package com.upgrade.interview.campsite.controller;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.service.AvailabilityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @ApiResponses(value = {
            @ApiResponse(code = HttpServletResponse.SC_OK, message = "Availabilities if there is one"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "Request processing error"),
            @ApiResponse(code = HttpServletResponse.SC_BAD_REQUEST, message = "Invalid date range: start date is greater than the end date")
    })
    @ApiOperation(value = "Availabilities for a given date range with the default being 1 month")
    @GetMapping("/availabilities")
    public Collection<AvailabilityDTO> availabilities(
            @ApiParam(value = "Start date range", name = "from", example = "YYYY-MM-DD")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate from,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @ApiParam(value = "End date range", name = "to", example = "YYYY-MM-DD")
            @RequestParam(required = false) LocalDate to) {
        LOGGER.info("About to get availabilities");
        return this.availabilityService.availabilities(from, to);
    }
}
