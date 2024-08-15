package com.cmpe275.AirlineReservationSystem.dto;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightDTO {

    private String flightNumber;

    private int price;

    private String origin;

    private String destination;

    private Date departureTime;

    private Date arrivalTime;

    private int seatsLeft;

    private String description;

    private PlaneDTO plane;

    private List<PassengerDTO> passengers;
}
