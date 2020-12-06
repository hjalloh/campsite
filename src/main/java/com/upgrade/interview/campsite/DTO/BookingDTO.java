package com.upgrade.interview.campsite.DTO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@ApiModel(value = "Booking representation")
public class BookingDTO {

    @ApiModelProperty(value = "Unique identifier", readOnly = true)
    private final Long uid;

    @ApiModelProperty(value = "Visitor's email", required = true)
    private String visitorEmail;

    @ApiModelProperty(value = "Visitor's full name", required = true)
    private String visitorFullName;

    @ApiModelProperty(value = "Intended arrival date", required = true, example = "YYYY-MM-DD")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate arrivalDate;

    @ApiModelProperty(value = "Intended departure date", required = true, example = "YYYY-MM-DD")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate departureDate;

    public BookingDTO(Long uid, String visitorEmail, String visitorFullName, LocalDate arrivalDate, LocalDate departureDate) {
        this.uid = uid;
        this.visitorEmail = visitorEmail;
        this.visitorFullName = visitorFullName;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public BookingDTO() {
        this(null, null, null, null, null);
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

    public Long getUid() {
        return uid;
    }

    public void setVisitorEmail(String visitorEmail) {
        this.visitorEmail = visitorEmail;
    }

    public void setVisitorFullName(String visitorFullName) {
        this.visitorFullName = visitorFullName;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    @Override
    public String toString() {
        return "BookingDTO{" +
                "uid='" + uid + '\'' +
                "visitorEmail='" + visitorEmail + '\'' +
                ", visitorFullName='" + visitorFullName + '\'' +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                '}';
    }
}
