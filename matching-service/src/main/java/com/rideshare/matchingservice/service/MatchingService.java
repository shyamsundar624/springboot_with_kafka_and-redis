package com.rideshare.matchingservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.client.LocationServiceClient;
import com.rideshare.matchingservice.dto.NearByDriverResponse;
import com.rideshare.matchingservice.event.RideMatchedEvent;
import com.rideshare.matchingservice.event.RideRequestEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

	private final LocationServiceClient locationServiceClient;
	private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;
	
	
	private final static String RIDE_MATCHED_TOPIC="ride.matched";
	
	private final static double DAFAULT_SEARCH_RADIUS_KM=5.0;
	
	/*
	 * Main matching algorithm
	 * called when RideRequestedEvent is consumed from kafka
	 * @param enve
	 * 
	 * STEP:
	 * 1. Ask Location service for nearby Driver
	 * 
	 * STEP2 Score each driver and pick the best one
	 */
	public void matchDriverForRide(RideRequestEvent event) {
		List<NearByDriverResponse> nearByDriver=locationServiceClient.getNearByDrivers(
				event.getPickupLatitude(),
				event.getPickupLongitude(),
				DAFAULT_SEARCH_RADIUS_KM
				);
		
		if(nearByDriver.isEmpty()) {
			log.warn("No drivers found near ride: ");
			return ;
		}
		
		//STEP2 Score each driver and pick the best one
		
		Optional<NearByDriverResponse> bestDriver=findBestDriver(nearByDriver);
		
		if(bestDriver.isEmpty()) {
			log.warn("Couldn't find suitabe driver for Ride");
			return ;
		}
		
		NearByDriverResponse assignedDriver=bestDriver.get();
		
		//STEP3: Publish rideMatched event to kafka
		RideMatchedEvent matchedEvent=new RideMatchedEvent(
				event.getRideId(),
				event.getRiderId(),
				assignedDriver.getDriverId(),
				assignedDriver.getLatitude(),
				assignedDriver.getLongitude(),
				assignedDriver.getDistanceInKm()
				
				);
		kafkaTemplate.send(RIDE_MATCHED_TOPIC,event.getRideId(),matchedEvent);
		
		log.info("Ride Matched Event Publish");
	}

	
	/*
	 * Driver Scoring algorithm
	 * 
	 * Distance 70%
	 * Rating 30%
	 * 
	 * Score= (1/distance)* distanceWeight +rating* ratingWeight
	 * 
	 */
	private Optional<NearByDriverResponse> findBestDriver(List<NearByDriverResponse> drivers) {
		
		double distanceWeight=0.7;
		double ratingWeight=0.3;
		
		return drivers.stream().max(Comparator.comparingDouble(driver->{
			//Distance score: closer=higher score
			//add 0.1 to avoid division by zero
			double distanceScore=1.0/(driver.getDistanceInKm());
			//simulated rating between 4.0 and 5.0
			//in Production: fetch from Driver Service
			double simulatedRating=4.0+Math.random();
			
			//final weighted score
			
			return (distanceScore*distanceWeight)+
					(simulatedRating*ratingWeight);
		}));
		 
	}
	
}
