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

@SuppressWarnings("javadoc")
public interface AirportRepository extends JpaRepository<Airport, String> {

    /**
     * Gets the total number of departures
     *
     * @return the total departures
     */
    @Query("select sum(departures) from Airport")
    int getTotalDepartures();

    /**
     * Gets the total number of arrivals
     *
     * @return the total arrivals
     */
    @Query("select sum(arrivals) from Airport")
    int getTotalArrivals();

    /**
     * Gets the total number of movements
     *
     * @return the total movements
     */
    @Query("select sum(movements) from Airport")
    int getTotalMovements();

}
