package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Service
public class AvailabilityService {

    @Autowired
    private BookingRepository bookingRepository;

    public Collection<AvailabilityDTO> availabilities(LocalDate from, LocalDate to) {
        //this.bookingRepository.f
        return Collections.emptyList();
    }
}
