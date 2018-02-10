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

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.service.FlightService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Post construct initializer for application.
 *
 * @author Richard Senior
 */
@Component
@Profile("development")
public class ApplicationInit implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplicationInit.class);

    @Autowired
    FlightService service;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Updating computed fields on all flights");
        service.findAllFlights().forEach(Flight::updateComputedFields);
    }

}
