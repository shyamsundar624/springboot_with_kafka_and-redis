package com.rideshare.rideservice.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rides")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ride {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@UuidGenerator
	@Column(updatable = false, nullable = false, length = 36)
	private String id;
	
	@Column(name = "rider_id", nullable = false)
	private String riderId;
	
	private String driverId;

	@Column(nullable = false)
	private double pickupLatitude;
	@Column(nullable = false)
	private double pickupLongitude;
	@Column(nullable = false)
	private String pickupAddress;

	@Column(nullable = false)
	private double dropLatitude;
	@Column(nullable = false)
	private double dropLongitude;
	@Column(nullable = false)
	private String dropAddress;

//ride status: REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
	@Enumerated(EnumType.STRING)
	private RideStatus status;

//Fare details
	private double estimatedFare;
	private double actualFare;

	// timestamps
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	private LocalDateTime startedAt;
	private LocalDateTime completedAt;

	
}
