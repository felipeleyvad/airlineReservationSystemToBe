package com.cmpe275.AirlineReservationSystem.controller;

import com.cmpe275.AirlineReservationSystem.Util.BadRequest;
import com.cmpe275.AirlineReservationSystem.Util.ExceptionHandle;
import com.cmpe275.AirlineReservationSystem.Util.Response;
import com.cmpe275.AirlineReservationSystem.dto.PassengerDTO;
import com.cmpe275.AirlineReservationSystem.service.PassengerService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

	@Autowired
	private PassengerService service;

	@PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
	@Transactional(rollbackOn = {IllegalArgumentException.class, ResponseStatusException.class})
	public ResponseEntity<?> updatePassenger(
			@PathVariable @NotNull String id,
			@RequestBody @Valid PassengerDTO passengerDTO) {
		try {
			return service.updatePassenger(id, passengerDTO);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, ex.getMessage())));
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@PostMapping(produces = "application/json", consumes = "application/json")
	@Transactional(rollbackOn = IllegalArgumentException.class)
	public ResponseEntity<?> createPassenger(
			@RequestBody @Valid PassengerDTO passengerDTO) {
		try {
			return service.createPassenger(passengerDTO);
		} catch (IllegalArgumentException ex) {
			return ResponseEntity
					.badRequest()
					.body(new ExceptionHandle(new BadRequest(400, ex.getMessage())));
		}
	}

	@GetMapping(produces = "application/json")
	public ResponseEntity<?> getPassenger(
			@RequestParam @Valid @NotNull String id) {
		try {
			return service.getPassenger(id);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
		}
	}

	@DeleteMapping(produces = "application/json")
	@Transactional(rollbackOn = ResponseStatusException.class)
	public ResponseEntity<?> deletePassenger(
			@RequestParam @Valid @NotNull String id) {
		try {
			service.deletePassenger(id);
			return ResponseEntity
					.ok(new Response(200, "Passenger with id " + id + " is deleted successfully"));
		} catch (Exception ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}
}
