package com.cmpe275.AirlineReservationSystem.entity;

import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Passenger")
public class Passenger {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	private String firstname;

	private String lastname;

	private int age;

	private String gender;

	@Column(unique = true)
	private String phone;

	@OneToMany(targetEntity = Reservation.class, cascade = CascadeType.ALL)
	@JsonIgnoreProperties({ "passenger", "price", "flights" })
	private List<Reservation> reservations;
}
