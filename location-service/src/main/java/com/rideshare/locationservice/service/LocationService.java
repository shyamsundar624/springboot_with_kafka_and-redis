package com.rideshare.locationservice.service;

import java.util.Collections;
import java.util.List;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rideshare.locationservice.dto.DriverLocationRequest;
import com.rideshare.locationservice.dto.NearByDriverResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

	private final RedisTemplate<String, String> redisTemplate;
	// redis key for drivers locations
	private static final String DRIVERS_GEO_KEY = "drivers:locations";
	/*
	 * update driver location in redis called every 3 seconds by drivers phone Maps
	 * to redis GEOADD command
	 */

	public void updateDriverLocation(DriverLocationRequest driverLocationRequest) {
		log.info("updating location for driver {}", driverLocationRequest.getDriverId());

		// Important longitude first and latitude second GEOSPATIAL standard

		Point driverpoint = new Point(driverLocationRequest.getLongitute(), driverLocationRequest.getLatitude());

		redisTemplate.opsForGeo().add(
				DRIVERS_GEO_KEY, 
				driverpoint, 
				driverLocationRequest.getDriverId());
		
		
		log.info("location updated for driver {}", driverLocationRequest.getDriverId());
	}

	/*
	 * find nearby drivers within given radiud
	 * called by matching Service on ride request
	 * Maps to redis GEORADIUS command
	 */
	
	public List<NearByDriverResponse> findNearbyDrivers(double latitude, double longitude, double radiusInKm) {  log.info("Finding nearby drivers for location ({}, {}) within radius {} km",
            latitude, longitude, radiusInKm);

    // Geospatial standard: Point takes (longitude, latitude) — NOT (lat, lon)
    Circle searchArea = new Circle(
            new Point(longitude, latitude),
            new Distance(radiusInKm, Metrics.KILOMETERS)
    );

    // ✅ includeCoordinates() is REQUIRED — without it, getPoint() returns null
    RedisGeoCommands.GeoRadiusCommandArgs args =
            RedisGeoCommands.GeoRadiusCommandArgs
                    .newGeoRadiusArgs()
                    .includeCoordinates()  // ← populates getPoint()
                    .includeDistance()     // ← populates getDistance()
                    .sortAscending()
                    .limit(10);

    GeoResults<RedisGeoCommands.GeoLocation<String>> result =
            redisTemplate.opsForGeo().radius(DRIVERS_GEO_KEY, searchArea, args);

    // ✅ Guard against null result (Redis key might not exist yet)
    if (result == null) {
        log.warn("No geo results returned — key '{}' may not exist in Redis yet",
                DRIVERS_GEO_KEY);
        return Collections.emptyList();
    }

    List<NearByDriverResponse> nearbyDrivers = result.getContent()
            .stream()
            .filter(geoResult -> geoResult.getContent().getPoint() != null) // ✅ extra safety
            .map(geoResult -> {
                String driverId = geoResult.getContent().getName();
                Point point     = geoResult.getContent().getPoint();
                double lat      = point.getY(); // Redis stores: X=longitude, Y=latitude
                double lon      = point.getX();
                double distance = geoResult.getDistance().getValue();

                return new NearByDriverResponse(driverId, lat, lon, distance);
            })
            .toList();

    log.info("Found {} nearby drivers for location ({}, {}) within radius {} km",
            nearbyDrivers.size(), latitude, longitude, radiusInKm);
    return nearbyDrivers;
	}
	
	
	/*
	 * remove driver location from redis called when driver goes offline or logs out
	 */
	
	public void removeDriver(String driverId) {
		
		
		log.info("removing location for driver {}", driverId);
		
		redisTemplate.opsForGeo().remove(DRIVERS_GEO_KEY, driverId);
		
		log.info("location removed for driver {}", driverId);
	}
	
}
