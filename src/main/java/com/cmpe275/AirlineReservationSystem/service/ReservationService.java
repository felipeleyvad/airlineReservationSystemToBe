package com.cmpe275.AirlineReservationSystem.service;

import com.cmpe275.AirlineReservationSystem.entity.Flight;
import com.cmpe275.AirlineReservationSystem.entity.Passenger;
import com.cmpe275.AirlineReservationSystem.entity.Reservation;
import com.cmpe275.AirlineReservationSystem.repository.FlightRepository;
import com.cmpe275.AirlineReservationSystem.repository.PassengerRepository;
import com.cmpe275.AirlineReservationSystem.repository.ReservationRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final PassengerRepository passengerRepository;
	private final FlightRepository flightRepository;

	public ResponseEntity<Reservation> getReservation(String id) throws NotFoundException {
		return reservationRepository.findById(id)
				.map(reservation -> new ResponseEntity<>(reservation, HttpStatus.OK))
				.orElseThrow(() -> new NotFoundException("Reservation with number " + id + " does not exist"));
	}

	public ResponseEntity<Reservation> createReservation(String passengerId, List<String> flightNumbers) {
		Passenger passenger = passengerRepository.findById(passengerId)
				.orElseThrow(() -> new IllegalArgumentException("Passenger not found with id " + passengerId));

		if (CollectionUtils.isEmpty(flightNumbers)) {
			throw new IllegalArgumentException("Flight numbers list must not be empty.");
		}

		List<String> trimmedFlightNumbers = flightNumbers.stream()
				.map(String::trim)
				.collect(Collectors.toList());

		List<Flight> flightList = getFlightList(trimmedFlightNumbers);

		if (flightList.size() > 1 && (isTimeOverlapWithinReservation(flightList) || isTimeOverlapForSamePerson(passengerId, flightList))) {
			throw new IllegalArgumentException("Time overlap detected in flight schedules.");
		}

		if (!isSeatsAvailable(flightList)) {
			throw new IllegalArgumentException("No seats available. Flight capacity full.");
		}

		int fare = calculatePrice(flightList);

		Reservation newReservation = new Reservation(
				flightList.get(0).getOrigin(),
				flightList.get(flightList.size() - 1).getDestination(),
				fare,
				passenger,
				new ArrayList<>(flightList) // Convertimos la lista a un conjunto para evitar duplicados
		);

		passenger.getReservations().add(newReservation);
		flightList.forEach(flight -> flight.getPassengers().add(passenger));

		reduceAvailableFlightSeats(flightList);
		Reservation savedReservation = reservationRepository.save(newReservation);
		return new ResponseEntity<>(savedReservation, HttpStatus.OK);
	}

	public ResponseEntity<Reservation> updateReservation(String number, List<String> flightsAdded, List<String> flightsRemoved) throws NotFoundException {
		Reservation existingReservation = reservationRepository.findByReservationNumber(number);

		if (existingReservation == null) {
			throw new NotFoundException("No reservation found for given reservation number");
		}

		if (!CollectionUtils.isEmpty(flightsRemoved)) {
			processFlightsToRemove(existingReservation, flightsRemoved);
		}

		if (!CollectionUtils.isEmpty(flightsAdded)) {
			processFlightsToAdd(existingReservation, flightsAdded);
		}

		Reservation updatedReservation = reservationRepository.save(existingReservation);
		return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
	}

	public ResponseEntity<String> cancelReservation(String reservationNumber) throws NotFoundException {
		Reservation reservation = reservationRepository.findByReservationNumber(reservationNumber);

		if (reservation == null) {
			throw new NotFoundException("Reservation with number " + reservationNumber + " does not exist");
		}

		reservation.getPassenger().getReservations().remove(reservation);
		reservationRepository.delete(reservation);

		// Aquí se corrige el problema, simplemente pasando el conjunto de vuelos
		increaseAvailableFlightSeats(reservation.getFlights());

		return new ResponseEntity<>("Reservation with number " + reservationNumber + " is canceled successfully", HttpStatus.OK);
	}

	private void processFlightsToRemove(Reservation existingReservation, List<String> flightsRemoved) {
		List<String> trimmedFlightsRemoved = flightsRemoved.stream().map(String::trim).collect(Collectors.toList());
		List<Flight> flightListToRemove = getFlightList(trimmedFlightsRemoved);

		if (existingReservation.getFlights().size() <= flightListToRemove.size()) {
			throw new IllegalArgumentException("Cannot update, Reservation has fewer or equal flights user trying to remove");
		}

		existingReservation.getFlights().removeAll(flightListToRemove);
		increaseAvailableFlightSeats(flightListToRemove);
		updateReservationDetails(existingReservation);
	}

	private void processFlightsToAdd(Reservation existingReservation, List<String> flightsAdded) {
		List<String> trimmedFlightsAdded = flightsAdded.stream().map(String::trim).collect(Collectors.toList());
		List<Flight> flightListToAdd = getFlightList(trimmedFlightsAdded);

		if (flightListToAdd.size() > 1 && (isTimeOverlapWithinReservation(flightListToAdd) || isTimeOverlapForSamePerson(existingReservation.getPassenger().getId(), flightListToAdd))) {
			throw new IllegalArgumentException("Cannot update, time overlap detected.");
		}

		if (!isSeatsAvailable(flightListToAdd)) {
			throw new IllegalArgumentException("No seats available. Flight capacity full.");
		}

		existingReservation.getFlights().addAll(flightListToAdd);
		reduceAvailableFlightSeats(flightListToAdd);
		updateReservationDetails(existingReservation);
	}

	private void updateReservationDetails(Reservation reservation) {
		if (!reservation.getFlights().isEmpty()) {
			reservation.setOrigin(reservation.getFlights().get(0).getOrigin());
			reservation.setDestination(reservation.getFlights().get(reservation.getFlights().size() - 1).getDestination());
			reservation.setPrice(calculatePrice(reservation.getFlights()));
		}
	}

	private boolean isTimeOverlapWithinReservation(List<Flight> flightList) {
		for (int i = 0; i < flightList.size(); i++) {
			for (int j = i + 1; j < flightList.size(); j++) {
				if (isTimeOverlap(flightList.get(i), flightList.get(j))) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean isTimeOverlapForSamePerson(String passengerId, List<Flight> flightList) {
		Passenger passenger = passengerRepository.findById(passengerId)
				.orElseThrow(() -> new IllegalArgumentException("Passenger not found with id " + passengerId));

		return passenger.getReservations().stream()
				.flatMap(reservation -> reservation.getFlights().stream())
				.anyMatch(existingFlight -> flightList.stream().anyMatch(newFlight -> isTimeOverlap(existingFlight, newFlight)));
	}

	private boolean isTimeOverlap(Flight flight1, Flight flight2) {
		return flight1.getDepartureTime().compareTo(flight2.getArrivalTime()) <= 0 &&
				flight1.getArrivalTime().compareTo(flight2.getDepartureTime()) >= 0;
	}

	private boolean isSeatsAvailable(List<Flight> flightList) {
		return flightList.stream().allMatch(flight -> flight.getSeatsLeft() > 0);
	}

	private List<Flight> getFlightList(List<String> flightNumbers) {
		return flightNumbers.stream()
				.map(flightRepository::getFlightByFlightNumber)
				.flatMap(Optional::stream)
				.collect(Collectors.toList());
	}

	public int calculatePrice(List<Flight> flightList) {
		return flightList.stream().mapToInt(Flight::getPrice).sum();
	}

	public void reduceAvailableFlightSeats(List<Flight> flightList) {
		flightList.forEach(flight -> flight.setSeatsLeft(flight.getSeatsLeft() - 1));
	}

	public void increaseAvailableFlightSeats(List<Flight> flightList) {
		flightList.forEach(flight -> flight.setSeatsLeft(flight.getSeatsLeft() + 1));
	}
}
