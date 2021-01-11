package com.upgrade.interview.campsite;

import com.upgrade.interview.campsite.entity.BookingEntity;
import com.upgrade.interview.campsite.repository.BookingRepository;
import com.upgrade.interview.campsite.utils.BookingStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CampsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampsiteApplication.class, args);
    }

    @Bean
    public CommandLineRunner data(BookingRepository repository) {
        return args -> {
            LocalDate start = LocalDate.now();
            LocalDate end = LocalDate.now().plusMonths(1);
            List<BookingEntity> bookings = new ArrayList<>();
            while (start.isBefore(end)) {
                BookingEntity booking = new BookingEntity();
                booking.setArrivalDate(start);
                booking.setDepartureDate(start.plusDays(1));
                booking.setStatus(BookingStatus.FREE.name());
                bookings.add(booking);
                start = start.plusDays(1);
            }
            repository.saveAll(bookings);
        };
    }
}
