package com.rideshare.rideservice.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.rideservice.dto.RideRequest;
import com.rideshare.rideservice.dto.RideResponse;
import com.rideshare.rideservice.event.RideRequestedEvent;
import com.rideshare.rideservice.model.Ride;
import com.rideshare.rideservice.model.RideStatus;
import com.rideshare.rideservice.repository.RideRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideService {

	private final RideRepository rideRepository;

	private final KafkaTemplate<String, RideRequestedEvent> kafkaTemplate;
	private static final String RIDE_REQUESTED_TOPIC = "ride.requested";

	/*
	 * create ride in DB with requested status
	 */

	public RideResponse requestRide(RideRequest request) {
		log.info(" New Ride request from rider: {}", request.getRiderId());

		// STEP 1: save ride to database

		Ride ride = new Ride();
		ride.setRiderId(request.getRiderId());
		ride.setPickupLatitude(request.getPickupLatitude());
		ride.setPickupLongitude(request.getPickupLongitude());
		ride.setPickupAddress(request.getPickupAddress());
		ride.setDropLatitude(request.getDropLatitude());
		ride.setDropLongitude(request.getDropLongitude());
		ride.setDropAddress(request.getDropAddress());
		ride.setStatus(RideStatus.REQUESTED);
		ride.setEstimatedFare(calculateEstimateFare(request));

		Ride savedRide = rideRepository.save(ride);

		// STEP 2: Publish Event to kafka
		// Matching Service will consume this an find nearest driver

		RideRequestedEvent event = new RideRequestedEvent(savedRide.getId(), savedRide.getRiderId(),
				savedRide.getPickupLatitude(), savedRide.getPickupLongitude(), savedRide.getPickupAddress(),
				savedRide.getDropLatitude(), savedRide.getDropLongitude(), savedRide.getDropAddress()

		);
		
		
		kafkaTemplate.send(RIDE_REQUESTED_TOPIC,savedRide.getId(),event);
		
		log.info("Ride Requested Event Publish to Kafka for ride: {}",savedRide.getId());
		
		//Update status to Matching
		savedRide.setStatus(RideStatus.MATCHING);
		rideRepository.save(savedRide);
		
		return mapToResponse(savedRide);

	}

	//called by Matching serve
	public void updateRideWithDriver(String rideId,String driverId) {
		Ride ride=rideRepository.findById(rideId).orElseThrow(()->new  RuntimeException("Ride Not FOund"));
		
		ride.setDriverId(driverId);
		ride.setStatus(RideStatus.ACCEPTED);
		rideRepository.save(ride);
	}
	
	
	public RideResponse startRide(String rideId) {
		Ride ride=rideRepository.findById(rideId).orElseThrow(()->new  RuntimeException("Ride Not FOund"));
		
		if(ride.getStatus()!=RideStatus.ACCEPTED) {
			throw new RuntimeException("Ride can't be Started. Current status: "+ride.getStatus());
		}
		
		
		ride.setStatus(RideStatus.RIDE_STARTED);
		ride.setStartedAt(LocalDateTime.now());
		rideRepository.save(ride);
		
		return mapToResponse(ride);
		
	}
	
	public RideResponse completeRide(String rideId) {
		Ride ride=rideRepository.findById(rideId).orElseThrow(()->new  RuntimeException("Ride Not FOund"));
		
		if(ride.getStatus()!=RideStatus.RIDE_STARTED) {
			throw new RuntimeException("Ride can't be Completed. Current status: "+ride.getStatus());
		}
		
		
		ride.setStatus(RideStatus.COMPLETED);
		ride.setCompletedAt(LocalDateTime.now());
		ride.setActualFare(ride.getEstimatedFare());
		rideRepository.save(ride);
		
		return mapToResponse(ride);
		
	}
	
	public RideResponse cancelRide(String rideId) {
		Ride ride=rideRepository.findById(rideId).orElseThrow(()->new  RuntimeException("Ride Not FOund"));
		
		ride.setStatus(RideStatus.CANCELLED);
		rideRepository.save(ride);
		
		return mapToResponse(ride);
		
	}
	
	
	public RideResponse getRideById(String rideId) {
		Ride ride=rideRepository.findById(rideId).orElseThrow(()->new  RuntimeException("Ride Not FOund"));
		
		return mapToResponse(ride);
		
	}
	
	public List<RideResponse> getRidesByRider(String riderId) {
	return	rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId).stream().map(this::mapToResponse)
		.collect(Collectors.toList());
	}
	
	private RideResponse mapToResponse(Ride ride) {
		RideResponse response=new RideResponse();
		response.setId(ride.getId());
		response.setRiderId(ride.getRiderId());
		response.setDriverId(ride.getDriverId());
		
		response.setPickupLatitude(ride.getPickupLatitude());
		response.setPickupLongitude(ride.getPickupLongitude());
		response.setPickupAddress(ride.getPickupAddress());
		
		response.setDropLatitude(ride.getDropLatitude());
		response.setDropLongitude(ride.getDropLongitude());
		response.setDropAddress(ride.getDropAddress());
		
		response.setStatus(ride.getStatus());
		response.setEstimatedFare(ride.getEstimatedFare());
		response.setActualFare(ride.getActualFare());
		response.setCompletedAt(ride.getCompletedAt());
		response.setStartedAt(ride.getStartedAt());
		response.setCompletedAt(ride.getCompletedAt());
		
		return response;
	}

	private double calculateEstimateFare(RideRequest request) {

		//Simplified Haversine distance calculation
		
		double lat1=Math.toRadians(request.getPickupLatitude());
		double lat2=Math.toRadians(request.getPickupLongitude());
		
		double lon1=Math.toRadians(request.getDropLatitude());
		double lon2=Math.toRadians(request.getDropLongitude());
		
		double dlat=lat2-lat1;
		double dlon=lon2-lon1;
		
		double a=Math.pow(Math.sin(dlat/2),2)+ 
				Math.cos(lat1)*Math.cos(lat2)
				*Math.pow(Math.sin(dlon/2), 2);
		
		double c=2*Math.asin(Math.sqrt(a));
		double distanceKm=6371*c;
		
		//Base Fare: 50Rs +12Rs per KM
		
		double fare=50+(distanceKm*12);
		return Math.round(fare*100.0)/100.0;
	}

}
