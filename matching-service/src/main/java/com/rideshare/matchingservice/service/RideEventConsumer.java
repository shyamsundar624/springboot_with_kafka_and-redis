package com.rideshare.matchingservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rideshare.matchingservice.event.RideRequestEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideEventConsumer {

	private final MatchingService matchingService;

	/*
	 * 
	 * Listen to ride.requested kafka Topic
	 * 
	 * Triggered every time ride service publishes a new ride request
	 * 
	 * FLOW: Ride Service-> Kafka(ride.requested) -> This Consumer ->
	 * MatchingService
	 * 
	 * 
	 */

	@KafkaListener(topics="ride.requested",
			groupId="matching-service-group")
	public void consumedRideRequestedEvent(RideRequestEvent event) {
		try {
			matchingService.matchDriverForRide(event);
		} catch (Exception e) {
			log.error("Error processing ride request: {} -> {}", event.getRideId(), e.getMessage());
			
			//In Production: send to dead letter queue for retry
		}
	}
}
