package com.cmpe275.AirlineReservationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaneDTO {

    private int capacity;
    private String model;
    private String manufacturer;
    private int yearOfManufacture;
}
