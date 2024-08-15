package com.cmpe275.AirlineReservationSystem.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Reservation")
@Data // Lombok genera getters, setters, toString, equals, y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Reservation {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String reservationNumber;

	private String origin;

	private String destination;

	private int price;

	@ManyToOne(targetEntity = Passenger.class, cascade = CascadeType.DETACH)
	@JsonIgnoreProperties({ "age", "gender", "phone", "reservations", "flight" })
	private Passenger passenger;

	@ManyToMany(targetEntity = Flight.class)
	@JsonIgnoreProperties({ "price", "seatsLeft", "description", "plane", "passengers" })
	private List<Flight> flights = new ArrayList<>();

	public Reservation(String origin, String destination, int price, Passenger passenger, List<Flight> flights) {
		this.origin = origin;
		this.destination = destination;
		this.price = price;
		this.passenger = passenger;
		this.flights = flights;
	}

}
