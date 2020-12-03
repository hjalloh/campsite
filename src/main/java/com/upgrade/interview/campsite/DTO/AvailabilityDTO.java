package com.upgrade.interview.campsite.DTO;

import java.time.LocalDate;

public class AvailabilityDTO {

    private final LocalDate start;
    private final LocalDate end;

    public AvailabilityDTO(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "AvailabilityDTO{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
