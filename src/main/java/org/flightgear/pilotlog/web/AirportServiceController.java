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

package org.flightgear.pilotlog.web;

import org.flightgear.pilotlog.domain.Airport;
import org.flightgear.pilotlog.dto.AirportInfo;
import org.flightgear.pilotlog.dto.Total;
import org.flightgear.pilotlog.dto.TotalsAwarePage;
import org.flightgear.pilotlog.service.AirportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Airport service controller for PilotLog.
 *
 * @author Richard Senior
 */
@RestController
@RequestMapping("/api")
public class AirportServiceController {

    private final AirportService airportService;

    public AirportServiceController(AirportService airportService) {
        this.airportService = airportService;
    }

    @GetMapping(path = "airports/", produces = APPLICATION_JSON_VALUE)
    public TotalsAwarePage<Airport> airports(
            @PageableDefault(sort = "movements", direction = DESC) Pageable pageable
    ) {
        Page<Airport> page = airportService.findAllAirports(pageable);
        Map<String, Total> totals = new HashMap<>();
        totals.put("arrivals", totalOf(Airport::getArrivals, page, airportService.getTotalArrivals()));
        totals.put("departures", totalOf(Airport::getDepartures, page, airportService.getTotalDepartures()));
        totals.put("movements", totalOf(Airport::getMovements, page, airportService.getTotalMovements()));
        return new TotalsAwarePage<>(page.getContent(), pageable, page.getTotalElements(), totals);
    }

    @GetMapping(path="airports.json", produces = APPLICATION_JSON_VALUE)
    public List<Airport> allAirports() {
        return airportService.findAllAirports();
    }

    @GetMapping(path = "airports/airport/{icao}", produces = APPLICATION_JSON_VALUE)
    public AirportInfo airportInfo(@PathVariable("icao") String icao) {
        return airportService.findAirportInfoByIcao(icao);
    }

    private Total<Integer> totalOf(ToIntFunction<Airport> function, Page<Airport> page, Integer grandTotal) {
        int pageTotal = page.getContent().parallelStream()
                .mapToInt(function)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

}
