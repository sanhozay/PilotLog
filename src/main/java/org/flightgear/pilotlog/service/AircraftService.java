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

package org.flightgear.pilotlog.service;

import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.integration.AircraftRepository;
import org.flightgear.pilotlog.integration.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Aircraft service.
 *
 * @author Richard Senior
 */
@Service
public class AircraftService {

    private static final Logger log = LoggerFactory.getLogger(FlightService.class);

    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;
    private final PageableUtil pageableUtil;

    public AircraftService(
            AircraftRepository aircraftRepository,
            FlightRepository flightRepository,
            PageableUtil pageableUtil
    ) {
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
        this.pageableUtil = pageableUtil;
    }

    @Transactional
    public void updateSummary(String model) {
        Aircraft summary = flightRepository.aircraftSummaryByModel(model);
        if (summary == null) {
            Aircraft aircraft = aircraftRepository.findAircraftByModel(model);
            if (aircraft != null) {
                aircraftRepository.delete(aircraft);
            }
        } else {
            aircraftRepository.save(summary);
        }
    }

    // Query methods

    @Transactional(readOnly = true)
    public List<Aircraft> findAllAircraft() {
        return aircraftRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Aircraft> findAllAircraft(Pageable pageable) {
        pageable = pageableUtil.adjustPageable(pageable, "model", "model");
        return aircraftRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public double getTotalDistance() {
        return aircraftRepository.getTotalDistance();
    }

    @Transactional(readOnly = true)
    public long getTotalDuration() {
        return aircraftRepository.getTotalDuration();
    }

    @Transactional(readOnly = true)
    public long getTotalFlights() {
        return aircraftRepository.getTotalFlights();
    }

    @Transactional(readOnly = true)
    public double getTotalFuel() {
        return aircraftRepository.getTotalFuel();
    }

}
