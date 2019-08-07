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
import org.flightgear.pilotlog.dto.Total;
import org.flightgear.pilotlog.dto.TotalsAwarePage;
import org.flightgear.pilotlog.service.AircraftService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
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

    private final AircraftService aircraftService;

    public AircraftServiceController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping(path = "aircraft/", produces = APPLICATION_JSON_VALUE)
    public TotalsAwarePage<Aircraft> aircraft(
            @PageableDefault(sort = "totalFlights", direction = DESC) Pageable pageable
    ) {
        Page<Aircraft> page = aircraftService.findAllAircraft(pageable);
        Map<String, Total> totals = new HashMap<>();
        totals.put("distance", totalOf(Aircraft::getTotalDistance, page, aircraftService.getTotalDistance()));
        totals.put("duration", totalOf(Aircraft::getTotalDuration, page, aircraftService.getTotalDuration()));
        totals.put("flights", totalOf(Aircraft::getTotalFlights, page, aircraftService.getTotalFlights()));
        totals.put("fuel", totalOf(Aircraft::getTotalFuel, page, aircraftService.getTotalFuel()));
        return new TotalsAwarePage<>(page.getContent(), pageable, page.getTotalElements(), totals);
    }

    private Total<Long> totalOf(ToLongFunction<Aircraft> function, Page<Aircraft> page, Long grandTotal) {
        if (page.getTotalPages() == 1) {
            long total = Math.round(grandTotal);
            return new Total<>(total, total);
        }
        long pageTotal = page.getContent().parallelStream()
                .mapToLong(function)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

    private Total<Long> totalOf(ToDoubleFunction<Aircraft> function, Page<Aircraft> page, Double grandTotal) {
        if (page.getTotalPages() == 1) {
            long total = Math.round(grandTotal);
            return new Total<>(total, total);
        }
        double pageTotal = page.getContent().parallelStream()
                .mapToDouble(function)
                .sum();
        return new Total<>(Math.round(pageTotal), Math.round(grandTotal));
    }

}
