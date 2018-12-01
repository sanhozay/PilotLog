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

package org.flightgear.pilotlog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

/**
 * Repository of flights.
 *
 * @author Richard Senior
 */
@SuppressWarnings("javadoc")
public interface FlightRepository extends JpaRepository<Flight, Integer> {

    /**
     * Finds flights with a given status.
     *
     * @param status the flight status
     * @return a set of flights with the given status
     * @see FlightStatus
     */
    Set<Flight> findByStatus(FlightStatus status);

    /**
     * Queries an aircraft summary.
     *
     * @param model the aircraft model, e.g. "707"
     * @return an aircraft summary
     */
    @Query("select new org.flightgear.pilotlog.domain.Aircraft(" +
            "f.aircraft," +
            "max(f.startTime), " +
            "min(f.distance), max(f.distance), sum(f.distance) / count(f.id), " +
            "min(f.fuelRate), max(f.fuelRate), sum(f.fuelUsed) * 3600 / sum(f.duration), " +
            "min(f.groundSpeed), max(f.groundSpeed), sum(f.distance) * 3600 / sum(f.duration), " +
            "sum(f.distance), " +
            "sum(f.duration), " +
            "count(f.id), " +
            "sum(f.fuelUsed)" +
            ") " +
            "from Flight f group by f.aircraft, f.status " +
            "having f.aircraft = :model and f.status = 'COMPLETE'")
    Aircraft aircraftSummaryByModel(@Param("model") String model);

    /**
     * Counts flights with a given origin airport
     * @param icao the origin airport
     * @return the number of flights with the airport as the origin
     */
    int countByOrigin(String icao);

    /**
     * Counts flights with a given destination airport
     * @param icao the destination airport
     * @return the number of flights with the airport as the destination
     */
    int countByDestination(String icao);

    /**
     * Gets the most recent departure from an airport
     *
     * @param icao the airport code
     * @return the most recent flight, or null if not found
     */
    Flight findFirstByOriginOrderByStartTimeDesc(String icao);

    /**
     * Gets the most recent arrival at an airport
     *
     * @param icao the airport code
     * @return the most recent flight, or null if not found
     */
    Flight findFirstByDestinationOrderByStartTimeDesc(String icao);

}
