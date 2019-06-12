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

package org.flightgear.pilotlog.integration;

import org.flightgear.pilotlog.domain.Aircraft;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository of aircraft summaries.
 *
 * @author Richard Senior
 */
public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {

    Aircraft findAircraftByModel(String model);

    /**
     * Gets the total distance
     *
     * @return the total distance
     */
    @Query("select coalesce(sum(totalDistance), 0) from Aircraft")
    int getTotalDistance();

    /**
     * Gets the total duration
     *
     * @return the total duration
     */
    @Query("select coalesce(sum(totalDuration), 0) from Aircraft")
    int getTotalDuration();

    /**
     * Gets the total flights
     *
     * @return the total flights
     */
    @Query("select coalesce(sum(totalFlights), 0) from Aircraft")
    int getTotalFlights();

    /**
     * Gets the total fuel
     *
     * @return the total fuel
     */
    @Query("select coalesce(sum(totalFuel), 0) from Aircraft")
    int getTotalFuel();

}
