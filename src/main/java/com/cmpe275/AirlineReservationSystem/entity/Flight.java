package com.cmpe275.AirlineReservationSystem.entity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Flight")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {

    @Id
    private String flightNumber;

    private int price;

    private String origin;

    private String destination;

    private Date departureTime;

    private Date arrivalTime;

    private int seatsLeft;

    private String description;

    @OneToOne(targetEntity = Plane.class, cascade = CascadeType.ALL)
    private Plane plane;

    @ManyToMany(targetEntity = Passenger.class)
    @JsonIgnoreProperties({ "age", "gender", "phone", "reservations", "flight" })
    private List<Passenger> passengers;

    @Override
    public int hashCode() {
        return Objects.hash(flightNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        Flight other = (Flight) obj;
        return Objects.equals(flightNumber, other.flightNumber);
    }
}
