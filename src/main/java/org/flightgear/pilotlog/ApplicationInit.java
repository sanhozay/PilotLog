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

package org.flightgear.pilotlog;

import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.service.AircraftService;
import org.flightgear.pilotlog.service.AirportService;
import org.flightgear.pilotlog.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Post construct initializer for application.
 *
 * @author Richard Senior
 */
@Component
public class ApplicationInit implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInit.class);

    private final AircraftService aircraftService;
    private final AirportService airportService;
    private final FlightService flightService;

    public ApplicationInit(
        AircraftService aircraftService,
        AirportService airportService,
        FlightService flightService
    ) {
        this.aircraftService = aircraftService;
        this.airportService = airportService;
        this.flightService = flightService;
    }

    @Override
    public void run(String... args) {
        log.info("Updating computed fields on completed flights");
        Set<String> aircraft = new HashSet<>();
        Set<String> airports = new HashSet<>();
        flightService.findByStatusWithTrack(FlightStatus.COMPLETE).forEach(flight -> {
            flightService.updateComputedFields(flight);
            flightService.updateTrackedStatus(flight);
            aircraft.add(flight.getAircraft());
            airports.add(flight.getOrigin());
            airports.add(flight.getDestination());
        });
        log.info("Updating aircraft summaries");
        aircraft.forEach(aircraftService::updateSummary);
        log.info("Updating airport summaries");
        airports.forEach(airportService::updateSummary);
        log.info("Retrospective updates complete");
    }

}
