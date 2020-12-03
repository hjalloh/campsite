package com.upgrade.interview.campsite.service;

import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Collection<BookingDTO> allBookings() {
        return bookingRepository.findAll().stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    private BookingDTO entityToDTO(BookingEntity entity) {
        return new BookingDTO(entity.getVisitorEmail(), entity.getVisitorFullName(), entity.getArrivalDate(), entity.getDepartureDate());
    }
}
