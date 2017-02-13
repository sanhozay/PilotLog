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

package org.flightgear.pilotlog.domain;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

/**
 * Repository of flights.
 *
 * @author Richard Senior
 */
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Finds flights by aircraft.
     *
     * @param aircraft the aircraft
     * @return a list of flights for a given aircraft
     */
    public List<Flight> findByAircraft(@Param("aircraft") String aircraft);

    /**
     * Finds flights by callsign.
     *
     * @param callsign the callsign
     * @return a list of flights for a given callsign
     */
    public List<Flight> findByCallsign(@Param("callsign") String callsign);

    /**
     * Finds flights by origin airport.
     *
     * @param origin the origin airport
     * @return a list of flights for a given origin airport
     */
    public List<Flight> findByOrigin(@Param("origin") String origin);

    /**
     * Finds flights by destination airport.
     *
     * @param destination the destination airport
     * @return a list of flights for a given destination airport
     */
    public List<Flight> findByDestination(@Param("destination") String destination);

    /**
     * Finds flights with a given status.
     *
     * @see FlightStatus
     * @param status the flight status
     * @return a set of flights with the given status
     */
    @RestResource(exported = false)
    public Set<Flight> findByStatus(FlightStatus status);

    /**
     * Gets the total flight time, in hours, for flights with a given status.
     *
     * @see FlightStatus
     * @param status the flight status
     * @return the total flight time of flights with the given status
     */
    @RestResource(exported = false)
    @Query("select sum(duration) from Flight f where f.status = :status")
    public Long findFlightTimeByStatus(@Param("status") FlightStatus status);

}
