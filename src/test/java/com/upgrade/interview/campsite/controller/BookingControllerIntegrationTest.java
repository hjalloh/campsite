package com.upgrade.interview.campsite.controller;

import com.upgrade.interview.campsite.DTO.BookingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private URL baseURL;

    @BeforeEach
    void setUp() throws MalformedURLException {
        baseURL = new URL("http://localhost:" + port + "/api.campsite/bookings");
    }

    @Test()
    public void testBook_should_throw_exception_when_arrival_date_is_after_departure_Date() {
        // GIVEN
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("visitorEmail", "hamidou.diallo@upgrade.com");
        body.put("visitorFullName", "Hamidou Diallo");
        body.put("arrivalDate", LocalDate.now().toString());
        body.put("departureDate", LocalDate.now().minusDays(2).toString());

        // WHEN
        ResponseEntity<Long> longResponseEntity = this.restTemplate.postForEntity(
                baseURL.toString(),
                new HttpEntity<>(body, headers),
                Long.class
        );


    }

    private BookingDTO booking(LocalDate arrivalDate, LocalDate departureDate) {
        return new BookingDTO(null, "hamidou.diallo@upgrade.com", "Hamidou Diallo", arrivalDate, departureDate);
    }

}