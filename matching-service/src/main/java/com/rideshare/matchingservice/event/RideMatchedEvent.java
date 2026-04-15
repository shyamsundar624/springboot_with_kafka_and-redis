package com.rideshare.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Event Published to Kafka Topic: ride.matched
 * Consume by Ride Service to update ride with assigned driver
 * 
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMatchedEvent {
	private String rideId;
	private String riderId;
	private String driverId;
	
	private double driverLatitude;
	private double driverLongitude;
	
	private double distanceToPickupKm;
	
}
