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

import org.flightgear.pilotlog.domain.Airport;
import org.flightgear.pilotlog.domain.AirportRepository;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Aircraft service.
 *
 * @author Richard Senior
 */
@Service
public class AirportService {

    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;
    private final PageableUtil pageableUtil;

    public AirportService(AirportRepository airportRepository,
        FlightRepository flightRepository,
        PageableUtil pageableUtil
    ) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
        this.pageableUtil = pageableUtil;
    }

    @Transactional
    public void updateSummary(String icao) {
        List<Flight> movements = flightRepository.findCompletedByOriginOrDestinationOrderByStartTimeDesc(icao);

        int departures = (int)movements.parallelStream()
            .filter(flight -> flight.getOrigin().equals(icao)).count();

        int arrivals = (int)movements.parallelStream()
            .filter(flight -> flight.getDestination() != null)
            .filter(flight -> flight.getDestination().equals(icao)).count();

        Optional<Airport> optional = airportRepository.findById(icao);
        if (optional.isPresent()) {
            Airport airport = optional.get();
            if (movements.size() == 0) {
                airportRepository.delete(airport);
            } else {
                airport.setArrivals(arrivals);
                airport.setDepartures(departures);
                airport.setLast(movements.get(0).getStartTime());
                airportRepository.save(airport);
            }
        } else if (movements.size() > 0) {
            Airport airport = new Airport(icao, arrivals, departures, movements.get(0).getStartTime());
            airportRepository.save(airport);
        }
    }

    // Query methods

    @Transactional(readOnly = true)
    public List<Airport> findAllAirports() {
        return airportRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Airport> findAllAirports(Pageable pageable) {
        pageable = pageableUtil.adjustPageable(pageable, "icao", "icao");
        return airportRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public int getTotalDepartures() {
        return airportRepository.getTotalDepartures();
    }

    @Transactional(readOnly = true)
    public int getTotalArrivals() {
        return airportRepository.getTotalArrivals();
    }

    @Transactional(readOnly = true)
    public int getTotalMovements() {
        return airportRepository.getTotalMovements();
    }


}
