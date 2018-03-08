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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.Total;
import org.flightgear.pilotlog.domain.TotalsAwarePage;
import org.flightgear.pilotlog.service.FlightNotFoundException;
import org.flightgear.pilotlog.service.FlightService;
import org.flightgear.pilotlog.service.InvalidFlightStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

/**
 * Flight service controller for PilotLog.
 *
 * @author Richard Senior
 */
@RestController
@RequestMapping("/api")
public class FlightServiceController {

    private final FlightService flightService;

    @Autowired
    public FlightServiceController(FlightService flightService) {
        this.flightService = flightService;
    }

    // Flightgear endpoints

    @GetMapping(path = "departure", produces = TEXT_XML_VALUE)
    public Flight departure(
            @RequestParam("callsign") String callsign,
            @RequestParam("aircraft") String aircraft,
            @RequestParam("airport") String airport,
            @RequestParam("altitude") float altitude,
            @RequestParam("fuel") float startFuel,
            @RequestParam("odometer") float startOdometer,
            @RequestParam("latitude") float latitude,
            @RequestParam("longitude") float longitude) {
        return flightService.beginFlight(
                callsign, aircraft, airport, altitude, startFuel,
                startOdometer, latitude, longitude
        );
    }

    @GetMapping(path = "arrival", produces = TEXT_XML_VALUE)
    public Flight arrival(
            @RequestParam("id") int id,
            @RequestParam("airport") String airport,
            @RequestParam("altitude") float altitude,
            @RequestParam("fuel") float endFuel,
            @RequestParam("odometer") float endOdometer,
            @RequestParam("latitude") float latitude,
            @RequestParam("longitude") float longitude)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return flightService.endFlight(
                id, airport, altitude, endFuel, endOdometer, latitude, longitude
        );
    }

    @GetMapping(path = "invalidate", produces = TEXT_XML_VALUE)
    public Flight invalidate(@RequestParam("id") int id)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return flightService.invalidateFlight(id);
    }

    @GetMapping(path = "pirep", produces = TEXT_XML_VALUE)
    public Flight pirep(
            @RequestParam("id") int id,
            @RequestParam("altitude") float altitude,
            @RequestParam("fuel") float fuel,
            @RequestParam("odometer") float odometer,
            @RequestParam("latitude") float latitude,
            @RequestParam("longitude") float longitude)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return flightService.updateFlight(id, altitude, fuel, odometer, latitude, longitude);
    }

    // Additional endpoints

    @PostMapping(path = "flights/", produces = {APPLICATION_JSON_VALUE})
    public TotalsAwarePage<Flight> flights(
            @RequestBody(required = false) Flight example,
            @PageableDefault(sort = "startTime", direction = DESC) Pageable pageable
    ) {
        List<Flight> matches = flightService.findFlightsByExample(example);
        Page<Flight> page = flightService.findFlightsByExample(example, pageable);
        Map<String, Total> totals = new HashMap<>();
        totals.put("duration", totalOf(Flight::getDuration, page, matches));
        return new TotalsAwarePage<>(page.getContent(), pageable, page.getTotalElements(), totals);
    }

    @DeleteMapping(path = "flights/flight/{id}")
    public void deleteFlight(@PathVariable int id) {
        flightService.deleteFlight(id);
    }

    @GetMapping(path = "flights.json", produces = {APPLICATION_JSON_VALUE})
    public List<Flight> flightsJSON() {
        return flightService.findAllFlights();
    }

    @GetMapping(path = "flights.xml", produces = {TEXT_XML_VALUE, APPLICATION_XML_VALUE})
    public List<Flight> flightsXML() {
        return flightService.findAllFlights();
    }

    @GetMapping(path = "flights.csv", produces = {"text/csv"})
    public String flightsCSV() throws JsonProcessingException {
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(Flight.class).withHeader();
        return mapper.writer(schema).writeValueAsString(flightService.findAllFlights());
    }

    private Total<Integer> totalOf(ToIntFunction<Flight> function, Page<Flight> page, List<Flight>
            matches) {
        int pageTotal = page.getContent().parallelStream()
                .filter(Flight::isComplete)
                .mapToInt(function)
                .sum();
        int grandTotal = matches.parallelStream()
                .filter(Flight::isComplete)
                .mapToInt(function)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

}
