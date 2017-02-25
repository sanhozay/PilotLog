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

package org.flightgear.pilotlog.domain

import spock.lang.Specification

/**
 * Tests for public methods of {@link Flight}
 *
 * @author Richard Senior
 */
class TestFlight extends Specification {

    def"Check fuel usage, duration and fuel rate are null for incomplete flights"() {

        given:"A new flight"
        def flight = new Flight("G-SHOZ", "pup100", "EGCJ", 12, 20)
        flight.startTime = new Date()

        expect:"computed fields to be null"
        flight.duration == null
        flight.fuelUsed == null
        flight.fuelRate == null
        flight.distance == null
        flight.reserve == null
    }

    def"Check fuel usage, duration and fuel rate for a completed flight"() {

        given:"A new flight"
        def flight = new Flight("G-SHOZ", "pup100", "EGCJ", 24, 0)
        flight.startTime = new Date()

        and:"flight has ended"
        flight.endTime = Date.from(flight.startTime.toInstant().plusSeconds(7200))
        flight.endFuel = 6
        flight.endOdometer = 200

        and:"fields have been computed"
        flight.updateComputedFields()

        expect:
        flight.duration == 120
        flight.fuelUsed == 18
        flight.fuelRate == 9
        flight.distance == 200
        flight.reserve == 40
    }

    def"Check fuel rate is null if fuel freeze is active"() {

        given:"A new flight"
        def flight = new Flight("G-SHOZ", "pup100", "EGCJ", 12, 20)

        and:"flight ended with no fuel consumption"
        flight.endFuel = flight.startFuel

        and:"fields have been computed"
        flight.updateComputedFields()

        expect:
        flight.fuelRate == null
    }

    def"Check no-argument constructor does not set status"() {

        given:"A new flight example"
        def flight = new Flight()

        expect:
        flight.status == null
    }
}
