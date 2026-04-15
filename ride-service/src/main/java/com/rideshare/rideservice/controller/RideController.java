package com.rideshare.rideservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.service.RideService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/rides")
@Slf4j
@RequiredArgsConstructor
public class RideController {

	private final RideService rideService;
	
	
	//Rider request a new ride
	@PostMapping("/request")
	public ResponseEntity<RideResponse> requestRide(@Valid @RequestBody RideRequest request){
		log.info("Ride Request received from rider: {}",request.getRiderId());
		
		return ResponseEntity.status(HttpStatus.CREATED).body(rideService.requestRide(request));
		
		
	}
	
	@GetMapping("/{rideId}")
	public ResponseEntity<RideResponse> getRideById(@PathVariable String rideId){
		return ResponseEntity.ok(rideService.getRideById(rideId));
	}
	
	@GetMapping("/rider/{riderId}")
	public ResponseEntity<List<RideResponse>> getRidesByRider(@PathVariable String riderId){
		
		return ResponseEntity.ok(rideService.getRidesByRider(riderId));
	}
	
	
	//Driver starts the ride
	@PutMapping("/{rideId}/start")
	public ResponseEntity<RideResponse> startRide(@PathVariable String rideId){
		return ResponseEntity.ok(rideService.startRide(rideId));
	}
	@PutMapping("/{rideId}/complete")
	public  ResponseEntity<RideResponse> completeRide(@PathVariable String rideId){
		return ResponseEntity.ok(rideService.completeRide(rideId));
	}
	
	
	@PutMapping("/{rideId}/cancel")
	public  ResponseEntity<RideResponse> canelRide(@PathVariable String rideId){
		return ResponseEntity.ok(rideService.cancelRide(rideId));
	}
}
