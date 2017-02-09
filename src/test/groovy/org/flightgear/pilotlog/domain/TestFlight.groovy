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

        expect:
        flight.duration == null
        flight.fuelUsed == null
        flight.fuelRate == null
    }

    def"Check fuel usage, duration and fuel rate for a completed flight"() {

        given:"A new flight"
        def flight = new Flight("G-SHOZ", "pup100", "EGCJ", 12, 20)
        flight.startTime = new Date()

        and:"Flight has ended"
        flight.endTime = Date.from(flight.startTime.toInstant().plusSeconds(7200))
        flight.endFuel = 10
        flight.endOdometer = 40

        and:"AOP has updated the duration"
        flight.updateComputedFields()

        expect:
        flight.duration == 120
        flight.fuelUsed == 2
        flight.fuelRate == 1
        flight.distance == 20
    }
}
