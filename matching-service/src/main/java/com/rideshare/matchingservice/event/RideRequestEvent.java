package com.rideshare.matchingservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Event Consume from kafka Topic ride.requested
 * publish by ride Service  when e rider request a ride
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestEvent {
	private String rideId;
	private String riderId;
	private String driverId;
	
	private double pickupLatitude;
	private double pickupLongitude;
	private String pickupAddress;

	private double dropLatitude;
	private double dropLongitude;
	private String dropAddress;
}
