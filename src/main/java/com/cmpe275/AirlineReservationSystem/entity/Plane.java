package com.cmpe275.AirlineReservationSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Plane")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plane {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	private String model;

	private int capacity;

	private String manufacturer;

	private int yearOfManufacture;

	public Plane(int capacity, String model, String manufacturer, int yearOfManufacture) {
		this.capacity = capacity;
		this.model = model;
		this.manufacturer = manufacturer;
		this.yearOfManufacture = yearOfManufacture;
	}
}
