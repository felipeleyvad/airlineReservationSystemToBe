package com.cmpe275.AirlineReservationSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpe275.AirlineReservationSystem.entity.Flight;

import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Integer> {

	/**
	 * To find a passenger by phone number
	 * 
	 * @param flightNumber
	 * @return
	 */
	Optional<Flight> getFlightByFlightNumber(String flightNumber);

}
