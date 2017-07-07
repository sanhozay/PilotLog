/*
 * PilotLog
 *
 * Copyright (c) 2017 Richard Senior
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

import java.util.List;

import org.flightgear.pilotlog.domain.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 *
 * @author Richard Senior
 */
public interface FlightService {

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
    Flight beginFlight(String callsign, String aircraft, String airport,
        float startFuel, float startOdometer);

    /**
     * Ends a flight.
     *
     * @param id the id of the flight that is to be ended
     * @param airport the ICAO code of the destination airport
     * @param endFuel the amount of fuel at landing, in US gallons
     * @param endOdometer the odometer reading at the end of the flight
     * @return the flight, with arrival fields updated
     */
    Flight endFlight(int id, String airport, float endFuel, float endOdometer);

    /**
     * Invalidates a flight.
     *
     * @param id the id of the flight to invalidate
     * @return the flight with status updated
     */
    Flight invalidateFlight(int id);

    /**
     * Updates a flight with a new altitude.
     * <p>
     * The altitude is only written into the flight if the rounded value is
     * greater than the current value.
     *
     * @param id the id of the flight to update
     * @param altitude the new altitude
     * @return the updated flight
     */
    Flight updateFlightAltitude(int id, double altitude);

    /**
     * Finds all flights.
     *
     * @return a list of all flights
     */
    public List<Flight> findAllFlights();

    /**
     * Gets flights that match an example flight, with paging and sorting
     * support.
     *
     * @param flight the example flight
     * @param pageable the pageable implementation for paging and sorting
     * @return a page of matching flights, according to the pageable
     */
    Page<Flight> findFlightsByExample(Flight flight, Pageable pageable);

    /**
     * Gets the total flight time, in hours, excluding invalid flights.
     *
     * @return the total flight time
     */
    int findFlightTimeTotal();

}
