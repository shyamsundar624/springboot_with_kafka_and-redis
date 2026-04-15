package com.rideshare.rideservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Event publish to kafka when a ride is requested
 * Matching service consume this event
 * TOPIC: ride.requested
 * 
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestedEvent {

	private String rideId;
	private String riderId;
	
	//pickup
	private double pickupLatitude;
	private double pickupLongitude;
	private String pickupAddress;

	//drop
	private double dropLatitude;
	private double dropLongitude;
	private String dropAddress;
	
	
}
