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

import org.flightgear.pilotlog.service.AircraftService;
import org.flightgear.pilotlog.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final FlightService flightService;

    @Autowired
    public ApplicationInit(AircraftService aircraftService, FlightService flightService) {
        this.aircraftService = aircraftService;
        this.flightService = flightService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Updating computed fields on all flights");
        Set<String> models = new HashSet<>();
        flightService.findAllFlights().forEach(flight -> {
            flight.updateComputedFields();
            models.add(flight.getAircraft());
        });
        models.forEach(aircraftService::updateSummary);
    }

}
