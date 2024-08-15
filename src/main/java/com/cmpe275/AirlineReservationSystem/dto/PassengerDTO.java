package com.cmpe275.AirlineReservationSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {

    @NotBlank
    private String firstname;

    @NotBlank
    private String lastname;

    @NotNull
    private String age;

    @NotBlank
    @Size(min = 1, max = 6)
    private String gender;

    @NotBlank
    private String phone;
}
