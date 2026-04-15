package com.rideshare.rideservice.model;
/*
 * FLOW
 * REQUESTED ->MATCHING-> ACCEPTED -> DRIVER_ARRIVING -
 *            ->RIDE_STARTED -> COMPLETED
 *            ->CANCELLED (can happen at any stage before COMPLETED)
 */
public enum RideStatus {
	REQUESTED,
	MATCHING,
	ACCEPTED,
	DRIVER_ARRIVING,
	RIDE_STARTED,
	COMPLETED,
	CANCELLED
}
