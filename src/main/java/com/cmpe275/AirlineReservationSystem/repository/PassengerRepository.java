package com.cmpe275.AirlineReservationSystem.repository;

import com.cmpe275.AirlineReservationSystem.entity.Passenger;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, String> {

	/**
	 * To find a passenger by phone number
	 * 
	 * @param phone
	 * @return
	 */
	Passenger findByPhone(String phone);

	/**
	 * To find a passenger by id
	 * 
	 * @param id
	 * @return
	 */
	Optional<Passenger> findById(String id);

}
