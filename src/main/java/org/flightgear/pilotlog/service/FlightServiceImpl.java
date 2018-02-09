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

import java.util.Date;
import java.util.List;

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.service.exceptions.FlightNotFoundException;
import org.flightgear.pilotlog.service.exceptions.InvalidFlightStatusException;
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

/**
 * Service for working with flights.
 *
 * @author Richard Senior
 */
@Service
@Transactional
public class FlightServiceImpl implements FlightService {

    private static final Logger log = LoggerFactory.getLogger(FlightServiceImpl.class);

    private final FlightRepository repository;

    @Autowired
    public FlightServiceImpl(FlightRepository repository) {
        this.repository = repository;
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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

    @Override
    public Flight updateFlightAltitude(int id, double altitude) {
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
        return flight;
    }

    // Query methods

    @Override
    @Transactional(readOnly = true)
    public List<Flight> findAllFlights() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Flight> findAllFlights(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Flight> findFlightsByExample(Flight flight, Pageable pageable) {
        final ExampleMatcher matcher = ExampleMatcher.matchingAll()
            .withIgnorePaths("id")
            .withIgnoreCase()
            .withIgnoreNullValues()
            .withStringMatcher(StringMatcher.STARTING);
        return repository.findAll(Example.of(flight, matcher), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public int findFlightTimeTotal() {
        final Integer total = repository.findFlightTimeByStatus(FlightStatus.COMPLETE);
        return total != null ? total : 0;
    }

}
