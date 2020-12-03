package com.upgrade.interview.campsite.repository;

import com.upgrade.interview.campsite.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
}
