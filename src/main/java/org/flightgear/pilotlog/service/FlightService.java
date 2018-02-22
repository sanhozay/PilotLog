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

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
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

import java.util.Date;
import java.util.List;

/**
 * Service for working with flights.
 *
 * @author Richard Senior
 */
@Service
@Transactional
public class FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    private final FlightRepository repository;
    private final PageableUtil pageableUtil;

    private final ExampleMatcher matcher = ExampleMatcher.matchingAll()
            .withIgnorePaths("id")
            .withIgnoreCase()
            .withIgnoreNullValues()
            .withStringMatcher(StringMatcher.STARTING);

    @Autowired
    public FlightService(FlightRepository repository, PageableUtil pageableUtil) {
        this.repository = repository;
        this.pageableUtil = pageableUtil;
    }

    /**
     * Begins a flight.
     *
     * @param callsign the callsign or registration
     * @param aircraft the aircraft model
     * @param airport the ICAO code of the departure airport
     * @param startFuel the amount of fuel at takeoff, in US gallons
     * @param startOdometer the odometer reading at the start of the flight
     * @return a new flight, with supplied fields and id field initialized
     */
    public Flight beginFlight(String callsign, String aircraft, String airport,
                              float startFuel, float startOdometer) {
        Flight flight = new Flight(callsign, aircraft, airport, startFuel, startOdometer);
        flight.setStartTime(new Date());
        flight.setStatus(FlightStatus.ACTIVE);
        flight = repository.save(flight);
        log.info("Started new flight {}", flight);
        return flight;
    }

    /**
     * Ends a flight.
     *
     * @param id the id of the flight that is to be ended
     * @param airport the ICAO code of the destination airport
     * @param endFuel the amount of fuel at landing, in US gallons
     * @param endOdometer the odometer reading at the end of the flight
     * @return the flight, with arrival fields updated
     */
    public Flight endFlight(int id, String airport, float endFuel, float endOdometer) {
        final Flight flight = repository.findOne(id);
        if (flight == null) {
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
        if (flight.getEndFuel() > flight.getStartFuel()) {
            flight.setStatus(FlightStatus.INVALID);
            log.warn("Invalidating flight {} because fuel has increased", flight.getId());
        }
        if (flight.getStatus().equals(FlightStatus.ACTIVE)) {
            flight.setStatus(FlightStatus.COMPLETE);
        }
        log.info("Ended flight {}", flight);
        return flight;
    }

    /**
     * Invalidates a flight.
     *
     * @param id the id of the flight to invalidate
     * @return the flight with status updated
     */
    public Flight invalidateFlight(int id) {
        final Flight flight = repository.findOne(id);
        if (flight == null) {
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
    public void deleteFlight(int id) {
        final Flight flight = repository.findOne(id);
        if (flight == null) {
            final String message = String.format("Attempt to delete flight with invalid id %d", id);
            throw new FlightNotFoundException(message);
        }
        if (!flight.getStatus().equals(FlightStatus.COMPLETE)) {
            final String message = String.format("Attempt to delete incomplete flight %s", flight);
            throw new InvalidFlightStatusException(message);
        }
        repository.delete(flight);
        log.info("Deleted flight {}", flight);
    }

    /**
     * Updates a flight.
     *
     * @param id the id of the flight to update
     * @param altitude the current altitude
     * @param fuel the current fuel level
     * @param odometer the current odometer
     * @return the updated flight
     */
    public Flight updateFlight(int id, float altitude, float fuel, float odometer) {
        final Flight flight = repository.findOne(id);
        if (flight == null) {
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
        return flight;
    }

    // Query methods

    @Transactional(readOnly = true)
    public List<Flight> findAllFlights() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Flight> findFlightsByExample(Flight example, Pageable pageable) {
        pageable = pageableUtil.adjustPageable(pageable, "id", "aircraft");
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

}
