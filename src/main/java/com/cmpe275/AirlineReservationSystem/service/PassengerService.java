package com.cmpe275.AirlineReservationSystem.service;

import com.cmpe275.AirlineReservationSystem.dto.PassengerDTO;
import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import com.cmpe275.AirlineReservationSystem.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PassengerService {

	@Autowired
	private PassengerRepository passengerRepository;

	@Autowired
	private ReservationRepository reservationRepository;

	/**
	 * Create a new passenger.
	 *
	 * @param passengerDTO Data Transfer Object containing passenger details
	 * @return ResponseEntity with the created Passenger and HTTP status
	 */
	public ResponseEntity<?> createPassenger(PassengerDTO passengerDTO) {
		Passenger existingPassenger = passengerRepository.findByPhone(passengerDTO.getPhone());

		if (existingPassenger == null) {
			Passenger newPassenger = convertToEntity(passengerDTO);
			Passenger savedPassenger = passengerRepository.save(newPassenger);
			return new ResponseEntity<>(savedPassenger, HttpStatus.OK);
		} else {
			throw new IllegalArgumentException("A passenger with the same phone number already exists.");
		}
	}

	/**
	 * Update an existing passenger's details.
	 *
	 * @param id           The ID of the passenger to be updated
	 * @param passengerDTO Data Transfer Object containing updated passenger details
	 * @return ResponseEntity with the updated Passenger and HTTP status
	 */
	public ResponseEntity<?> updatePassenger(String id, PassengerDTO passengerDTO) {
		Optional<Passenger> existingPassOpt = passengerRepository.findById(id);

		if (existingPassOpt.isPresent()) {
			Passenger existingPassenger = passengerRepository.findByPhone(passengerDTO.getPhone());

			if (existingPassenger == null || existingPassenger.getId().equals(id)) {
				Passenger passenger = existingPassOpt.get();
				updateEntityFromDTO(passenger, passengerDTO);
				Passenger updatedPassenger = passengerRepository.save(passenger);
				return new ResponseEntity<>(updatedPassenger, HttpStatus.OK);
			} else {
				throw new IllegalArgumentException("A passenger with the same phone number already exists.");
			}
		} else {
			throw new EntityNotFoundException("Passenger with ID " + id + " does not exist.");
		}
	}

	/**
	 * Delete a passenger and their reservations.
	 *
	 * @param id The ID of the passenger to be deleted
	 * @return ResponseEntity with the deletion status and HTTP status
	 */
	public ResponseEntity<?> deletePassenger(String id) {
		Optional<Passenger> existingPassOpt = passengerRepository.findById(id);

		if (existingPassOpt.isPresent()) {
			Passenger passenger = existingPassOpt.get();
			List<Reservation> reservations = reservationRepository.findByPassenger(passenger);

			for (Reservation reservation : reservations) {
				deleteReservation(reservation, passenger);
			}

			passengerRepository.deleteById(id);
			return new ResponseEntity<>("Passenger with ID " + id + " is deleted successfully.", HttpStatus.OK);
		} else {
			throw new EntityNotFoundException("Passenger with ID " + id + " does not exist.");
		}
	}

	/**
	 * Get passenger details by ID.
	 *
	 * @param id The ID of the passenger to be retrieved
	 * @return ResponseEntity with the Passenger details and HTTP status
	 */
	public ResponseEntity<?> getPassenger(String id) {
		Optional<Passenger> existingPassOpt = passengerRepository.findById(id);
		if (existingPassOpt.isPresent()) {
			Passenger passenger = existingPassOpt.get();
			return new ResponseEntity<>(passenger, HttpStatus.OK);
		} else {
			throw new EntityNotFoundException("Passenger with ID " + id + " does not exist.");
		}
	}

	/**
	 * Delete a reservation associated with a passenger.
	 *
	 * @param reservation The reservation to be deleted
	 * @param passenger   The passenger associated with the reservation
	 */
	private void deleteReservation(Reservation reservation, Passenger passenger) {
		for (Flight flight : reservation.getFlights()) {
			updateFlightSeats(flight);
			flight.getPassengers().remove(passenger);
		}

		passenger.getReservations().remove(reservation);
		reservationRepository.delete(reservation);
	}

	/**
	 * Update the number of seats left on a flight.
	 *
	 * @param flight The flight whose seats need to be updated
	 */
	private void updateFlightSeats(Flight flight) {
		flight.setSeatsLeft(flight.getSeatsLeft() + 1);
	}

	/**
	 * Convert a PassengerDTO to a Passenger entity.
	 *
	 * @param passengerDTO The Data Transfer Object to be converted
	 * @return The corresponding Passenger entity
	 */
	private Passenger convertToEntity(PassengerDTO passengerDTO) {
		return Passenger.builder()
				.firstname(passengerDTO.getFirstname())
				.lastname(passengerDTO.getLastname())
				.age(Integer.parseInt(passengerDTO.getAge()))
				.gender(passengerDTO.getGender())
				.phone(passengerDTO.getPhone())
				.build();
	}

	/**
	 * Update an existing Passenger entity using data from a PassengerDTO.
	 *
	 * @param passenger    The Passenger entity to be updated
	 * @param passengerDTO The Data Transfer Object containing updated data
	 */
	private void updateEntityFromDTO(Passenger passenger, PassengerDTO passengerDTO) {
		passenger.setFirstname(passengerDTO.getFirstname());
		passenger.setLastname(passengerDTO.getLastname());
		passenger.setAge(Integer.parseInt(passengerDTO.getAge()));
		passenger.setGender(passengerDTO.getGender());
		passenger.setPhone(passengerDTO.getPhone());
	}
}
