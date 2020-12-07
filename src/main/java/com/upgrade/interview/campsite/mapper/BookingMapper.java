package com.upgrade.interview.campsite.mapper;

import com.upgrade.interview.campsite.DTO.BookingDTO;
import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.utils.BookingStatus;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingDTO entityToDTO(BookingEntity entity) {
        return new BookingDTO(entity.getId(), entity.getVisitorEmail(), entity.getVisitorFullName(), entity.getArrivalDate(), entity.getDepartureDate());
    }

    public BookingEntity dtoToEntity(BookingDTO dto) {
        return new BookingEntity(dto.getVisitorEmail(), dto.getVisitorFullName(), dto.getArrivalDate(), dto.getDepartureDate(), BookingStatus.RESERVED.name());
    }
}
