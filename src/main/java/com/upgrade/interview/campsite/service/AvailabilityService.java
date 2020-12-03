package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.AvailabilityDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;

@Service
public class AvailabilityService {
    public Collection<AvailabilityDTO> availabilities(LocalDate from, LocalDate to) {
        return Collections.emptyList();
    }
}
