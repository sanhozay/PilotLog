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

import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.domain.Total;
import org.flightgear.pilotlog.domain.TotalsAwarePage;
import org.flightgear.pilotlog.service.AircraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Aircraft service controller for PilotLog.
 *
 * @author Richard Senior
 */
@RestController
@RequestMapping("/api")
public class AircraftServiceController {

    @Autowired
    private AircraftService aircraftService;

    @GetMapping(path = "aircraft/", produces = APPLICATION_JSON_VALUE)
    public TotalsAwarePage<Aircraft> aircraft(
            @PageableDefault(sort = "totalFlights", direction = DESC) Pageable pageable
    ) {
        List<Aircraft> all = aircraftService.findAllAircraft();
        Page<Aircraft> page = aircraftService.findAllAircraft(pageable);
        Map<String, Total> totals = new HashMap<>();
        totals.put("distance", totalOf(Aircraft::getTotalDistance, all, page));
        totals.put("duration", totalOf(Aircraft::getTotalDuration, all, page));
        totals.put("flights", totalOf(Aircraft::getTotalFlights, all, page));
        totals.put("fuel", totalOf(Aircraft::getTotalFuel, all, page));
        return new TotalsAwarePage<>(page.getContent(), pageable, page.getTotalElements(), totals);
    }

    private Total<Long> totalOf(ToLongFunction<Aircraft> function, List<Aircraft> all, Page<Aircraft> page) {
        long grandTotal = all.parallelStream()
                .mapToLong(function)
                .sum();
        long pageTotal = page.getContent().parallelStream()
                .mapToLong(function)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

    private Total<Double> totalOf(ToDoubleFunction<Aircraft> function, List<Aircraft> all, Page<Aircraft> page) {
        double grandTotal = all.parallelStream()
                .mapToDouble(function)
                .sum();
        double pageTotal = page.getContent().parallelStream()
                .mapToDouble(function)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

}
