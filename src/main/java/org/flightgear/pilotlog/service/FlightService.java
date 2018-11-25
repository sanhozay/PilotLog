/*
 * PilotLog
 *
 * Copyright © 2018 Richard Senior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.flightgear.pilotlog.service;

import org.flightgear.pilotlog.domain.Coordinate;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.domain.TrackPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Service for working with flights.
 *
 * @author Richard Senior
 */
@Service
@SuppressWarnings("WeakerAccess")
public class FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    private final FlightRepository repository;
    private final AircraftService aircraftService;

    private final ExampleMatcher matcher = ExampleMatcher.matchingAll()
            .withIgnorePaths("id")
            .withIgnoreCase()
            .withIgnoreNullValues()
            .withStringMatcher(StringMatcher.STARTING);
    private PageableUtil pageableUtil;

    public FlightService(FlightRepository repository, AircraftService aircraftService) {
        this.repository = repository;
        this.aircraftService = aircraftService;
    }

    /**
     * Begins a flight.
     *
     * @param callsign the callsign or registration
     * @param aircraft the aircraft model
     * @param airport the ICAO code of the departure airport
     * @param altitude the altitude at departure
     * @param startFuel the amount of fuel at takeoff, in US gallons
     * @param startOdometer the odometer reading at the start of the flight
     * @param latitude the latitude at the start of the flight
     * @param longitude the longitude at the start of the flight
     * @return a new flight, with supplied fields and id field initialized
     */
    @Transactional
    public Flight beginFlight(String callsign, String aircraft, String airport, float altitude,
                              float startFuel, float startOdometer, float latitude, float longitude) {
        Flight flight = new Flight(callsign, aircraft, airport, startFuel, startOdometer);
        flight.setStartTime(new Date());
        flight.setStatus(FlightStatus.ACTIVE);

        Coordinate coordinate = new Coordinate(latitude, longitude);
        TrackPoint trackPoint = new TrackPoint(altitude, startFuel, startOdometer, coordinate);
        trackPoint.setTimestamp(new Date());
        flight.addTrackPoint(trackPoint);

        flight = repository.save(flight);
        log.info("Started new flight {}", flight);
        return flight;
    }

    /**
     * Ends a flight.
     *
     * @param id the id of the flight that is to be ended
     * @param airport the ICAO code of the destination airport
     * @param altitude the altitude at arrival
     * @param endFuel the amount of fuel at landing, in US gallons
     * @param endOdometer the odometer reading at the end of the flight
     * @param latitude the latitude at the start of the flight
     * @param longitude the longitude at the start of the flight
     * @return the flight, with arrival fields updated
     */
    @Transactional
    public Flight endFlight(int id, String airport, float altitude, float endFuel,
                            float endOdometer, float latitude, float longitude) {
        final Flight flight;
        try {
            flight = findFlightById(id);
        } catch (FlightNotFoundException e) {
            final String message = String.format("Attempt to end flight with invalid id %d", id);
            throw new FlightNotFoundException(message);
        }
        if (flight.getStatus().equals(FlightStatus.COMPLETE)) {
            final String message = String.format("Attempt to end completed flight %s", flight);
            throw new InvalidFlightStatusException(message);
        }
        flight.setDestination(airport);
        flight.setEndFuel(endFuel);
        flight.setEndOdometer(endOdometer);
        flight.setEndTime(new Date());
        if (flight.getEndFuel() >= flight.getStartFuel()) {
            flight.setStatus(FlightStatus.INVALID);
            log.warn("Invalidated flight #{} because fuel has not decreased", flight.getId());
        }
        if (flight.getStatus().equals(FlightStatus.ACTIVE)) {
            flight.setStatus(FlightStatus.COMPLETE);
        }

        Coordinate coordinate = new Coordinate(latitude, longitude);
        TrackPoint trackPoint = new TrackPoint(altitude, endFuel, endOdometer, coordinate);
        trackPoint.setTimestamp(new Date());
        flight.addTrackPoint(trackPoint);

        log.info("Ended flight {}", flight);
        return flight;
    }

    /**
     * Invalidates a flight.
     *
     * @param id the id of the flight to invalidate
     * @return the flight with status updated
     */
    @Transactional
    public Flight invalidateFlight(int id) {
        final Flight flight;
        try {
            flight = findFlightById(id);
        } catch (FlightNotFoundException e) {
            final String message = String.format("Attempt to invalidate flight with invalid id %d", id);
            throw new FlightNotFoundException(message);
        }
        if (flight.getStatus().equals(FlightStatus.COMPLETE)) {
            final String message = String.format("Attempt to invalidate completed flight %s", flight);
            throw new InvalidFlightStatusException(message);
        }
        flight.setStatus(FlightStatus.INVALID);
        log.warn("Invalidated flight {}", flight);
        return flight;
    }

    /**
     * Deletes a flight.
     *
     * @param id the id of the flight to delete
     */
    @Transactional
    public void deleteFlight(int id) {
        final Flight flight;
        try {
            flight = findFlightById(id);
        } catch (FlightNotFoundException e) {
            final String message = String.format("Attempt to delete flight with invalid id %d", id);
            throw new FlightNotFoundException(message);
        }
        if (!flight.getStatus().equals(FlightStatus.COMPLETE)) {
            final String message = String.format("Attempt to delete incomplete flight %s", flight);
            throw new InvalidFlightStatusException(message);
        }
        repository.delete(flight);
        aircraftService.updateSummary(flight.getAircraft());
        log.info("Deleted flight {}", flight);
    }

    /**
     * Updates a flight.
     *
     * @param id the id of the flight to update
     * @param altitude the current altitude
     * @param fuel the current fuel level
     * @param odometer the current odometer
     * @param latitude the current latitude
     * @param longitude the current longitude
     * @param heading the current heading
     * @return the updated flight
     */
    @Transactional
    public Flight updateFlight(int id, float altitude, float fuel,
                               float odometer, float latitude, float longitude, float heading) {
        final Flight flight;
        try {
            flight = findFlightById(id);
        } catch (FlightNotFoundException e) {
            final String message = String.format("Attempt to update flight with invalid id %d", id);
            throw new FlightNotFoundException(message);
        }
        if (flight.getStatus().equals(FlightStatus.COMPLETE)) {
            final String message = String.format("Attempt to update completed flight %s", flight);
            throw new InvalidFlightStatusException(message);
        }
        final int a = 100 * ((int)altitude / 100);
        if (flight.getAltitude() == null || a > flight.getAltitude()) {
            flight.setAltitude(a);
            log.info("Updated altitude to {} on flight {}", a, flight);
        }
        flight.setEndFuel(fuel);
        flight.setEndOdometer(odometer);
        flight.setEndTime(new Date());
        flight.setHeading(heading);

        Coordinate coordinate = new Coordinate(latitude, longitude);
        TrackPoint trackPoint = new TrackPoint(altitude, fuel, odometer, coordinate);
        trackPoint.setTimestamp(new Date());
        flight.addTrackPoint(trackPoint);

        return flight;
    }

    /**
     * Updates computed fields of flight; duration, fuelUsed and fuelRate.
     *
     * @param flight the flight to update
     */
    @Transactional
    public void updateComputedFields(Flight flight) {
        if (flight.getStartTime() != null && flight.getEndTime() != null) {
            int duration = (int)Duration.between(
                    flight.getStartTime().toInstant(),
                    flight.getEndTime().toInstant()
            ).getSeconds();
            if (duration == 0 && flight.isComplete()) {
                flight.setStatus(FlightStatus.INVALID);
                log.warn("Invalidating flight {} because duration is zero", flight);
            }
            flight.setDuration(duration);
        }
        if (flight.getStartFuel() != null && flight.getEndFuel() != null) {
            flight.setFuelUsed(flight.getStartFuel() - flight.getEndFuel());
        }
        if (flight.getStartOdometer() != null && flight.getEndOdometer() != null) {
            flight.setDistance(flight.getEndOdometer() - flight.getStartOdometer());
        }
        if (flight.getFuelUsed() != null && flight.getDuration() != null && flight.getDuration() >= 10) {
            flight.setFuelRate(3600 * flight.getFuelUsed() / flight.getDuration());
        }
        if (flight.getEndFuel() != null && flight.getFuelRate() != null && flight.getFuelRate() > 0.0) {
            flight.setReserve(3600 * flight.getEndFuel() / flight.getFuelRate());
        }
        if (flight.getDistance() != null && flight.getDuration() != null && flight.getDuration() >= 10) {
            flight.setGroundSpeed((int)(flight.getDistance() / (flight.getDuration() / 3600.0)));
        }
    }

    /**
     * Purges incomplete and invalid flights.
     */
    @Transactional
    public void purge() {
        Set<Flight> purgeable = new HashSet<>();
        purgeable.addAll(repository.findByStatus(FlightStatus.ACTIVE));
        purgeable.addAll(repository.findByStatus(FlightStatus.INVALID));
        for (Flight flight : purgeable) {
            repository.delete(flight);
            log.info("Deleted flight {}", flight);
        }
    }

    // Query methods

    @Transactional(readOnly = true)
    public Flight findFlightById(int id) {
        Optional<Flight> optional = repository.findById(id);
        if (!optional.isPresent()) {
            String message = String.format("Could not find flight by id %d", id);
            throw new FlightNotFoundException(message);
        }
        return optional.get();
    }

    @Transactional(readOnly = true)
    public List<Flight> findAllFlights() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Flight> findFlightsByExample(Flight example, Pageable pageable) {
        if (pageableUtil != null) {
            pageable = pageableUtil.adjustPageable(pageable, "id", "aircraft");
        }
        if (example != null) {
            return repository.findAll(Example.of(example, matcher), pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

    @Transactional(readOnly = true)
    public List<Flight> findFlightsByExample(Flight example) {
        if (example != null) {
            return repository.findAll(Example.of(example, matcher));
        } else {
            return repository.findAll();
        }
    }

    // Accessors

    @Autowired(required = false)
    public void setPageableUtil(PageableUtil pageableUtil) {
        this.pageableUtil = pageableUtil;
    }

}
