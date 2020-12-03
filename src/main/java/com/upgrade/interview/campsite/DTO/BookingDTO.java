package com.upgrade.interview.campsite.DTO;

import java.time.LocalDate;

public class BookingDTO {

    private final String visitorEmail;
    private final String visitorFullName;
    private final LocalDate arrivalDate;
    private final LocalDate departureDate;

    public BookingDTO(String visitorEmail, String visitorFullName, LocalDate arrivalDate, LocalDate departureDate) {
        this.visitorEmail = visitorEmail;
        this.visitorFullName = visitorFullName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public String getVisitorFullName() {
        return visitorFullName;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    @Override
    public String toString() {
        return "BookingDTO{" +
                "visitorEmail='" + visitorEmail + '\'' +
                ", visitorFullName='" + visitorFullName + '\'' +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                '}';
    }
}
