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

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightStatus;

import java.util.Date;

import static org.flightgear.pilotlog.integration.FlightRepositoryTest.HOUR;
import static org.flightgear.pilotlog.domain.FlightStatus.COMPLETE;
import static org.flightgear.pilotlog.domain.FlightStatus.NEW;

@SuppressWarnings("WeakerAccess")
class FlightBuilder {

    private String aircraft, origin, destination;
    private FlightStatus status;
    private Integer duration;
    private Float fuelUsed, distance;

    FlightBuilder(String aircraft, String origin) {
        this.aircraft = aircraft;
        this.origin = origin;
        this.status = NEW;
    }

    FlightBuilder complete(String destination, int duration, float fuel, float distance) {
        return status(COMPLETE).destination(destination).duration(duration).fuel(fuel).distance(distance);
    }

    FlightBuilder destination(String destination) {
        this.destination = destination;
        return this;
    }

    FlightBuilder distance(float distance) {
        this.distance = distance;
        return this;
    }

    FlightBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    FlightBuilder fuel(float fuelUsed) {
        this.fuelUsed = fuelUsed;
        return this;
    }

    FlightBuilder status(FlightStatus status) {
        this.status = status;
        return this;
    }

    Flight build() {
        Flight flight = new Flight("G-SHOZ", aircraft, origin, 1000.0f, 0.0f);
        flight.setStartTime(new Date(0L));
        flight.setStatus(status);
        flight.setAltitude(10000);
        if (distance != null) {
            flight.setEndOdometer(flight.getStartOdometer() + distance);
            flight.setDistance(distance);
        }
        if (duration != null) {
            flight.setEndTime(new Date((long)duration));
            flight.setDuration(duration);
        }
        if (fuelUsed != null) {
            flight.setEndFuel(flight.getStartFuel() - fuelUsed);
            flight.setFuelUsed(fuelUsed);
        }
        if (distance != null && duration != null) {
            flight.setGroundSpeed((int)(HOUR * distance / duration));
        }
        if (fuelUsed != null && duration != null) {
            flight.setFuelRate(fuelUsed * HOUR / duration);
            flight.setReserve(flight.getEndFuel() / flight.getFuelRate());
        }
        flight.setDestination(destination);
        return flight;
    }

}
