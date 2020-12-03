package com.upgrade.interview.campsite.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "booking", uniqueConstraints = @UniqueConstraint(columnNames = {"arrival_date", "departure_date"}))
public class BookingEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "visitor_email", nullable = false)
    private String visitorEmail;

    @Column(name = "visitor_full_name", nullable = false)
    private String visitorFullName;

    @Column(name = "arrival_date", columnDefinition = "DATE", nullable = false)
    private LocalDate arrivalDate;

    @Column(name = "departure_date", columnDefinition = "DATE", nullable = false)
    private LocalDate departureDate;

    @Column(nullable = false)
    private String status;

    public BookingEntity() {
    }

    public BookingEntity(String visitorEmail, String visitorFullName, LocalDate arrivalDate, LocalDate departureDate, String status) {
        this.visitorEmail = visitorEmail;
        this.visitorFullName = visitorFullName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVisitorEmail() {
        return visitorEmail;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public String getVisitorFullName() {
        return visitorFullName;
    }

    public void setVisitorFullName(String visitorFullName) {
        this.visitorFullName = visitorFullName;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BookingEntity that = (BookingEntity) o;
        return id.equals(that.id) &&
                visitorEmail.equals(that.visitorEmail) &&
                visitorFullName.equals(that.visitorFullName) &&
                arrivalDate.equals(that.arrivalDate) &&
                departureDate.equals(that.departureDate) &&
                status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, visitorEmail, visitorFullName, arrivalDate, departureDate, status);
    }

    @Override
    public String toString() {
        return "BookingEntity{" +
                "id=" + id +
                ", visitorEmail='" + visitorEmail + '\'' +
                ", visitorFullName='" + visitorFullName + '\'' +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", status='" + status + '\'' +
                '}';
    }
}
