package com.cmpe275.AirlineReservationSystem.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private String reservationNumber;

    private String origin;

    private String destination;

    private int price;

    private PassengerDTO passenger;

    private List<FlightDTO> flights;
}
