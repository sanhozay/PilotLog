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
import org.flightgear.pilotlog.service.FlightService;
import org.flightgear.pilotlog.service.exceptions.FlightNotFoundException;
import org.flightgear.pilotlog.service.exceptions.InvalidFlightStatusException;
import org.flightgear.pilotlog.domain.DurationTotalsAwarePage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

/**
 * Web service controller for PilotLog.
 *
 * @author Richard Senior
 */
@RestController
@RequestMapping("/api")
public class ServiceController {

    @Autowired
    private FlightService service;

    // Flightgear endpoints

    @GetMapping(path = "departure", produces = TEXT_XML_VALUE)
    public Flight departure(
            @RequestParam("callsign") String callsign,
            @RequestParam("aircraft") String aircraft,
            @RequestParam("airport") String airport,
            @RequestParam("fuel") float startFuel,
            @RequestParam("odometer") float startOdometer) {
        return service.beginFlight(callsign, aircraft, airport, startFuel, startOdometer);
    }

    @GetMapping(path = "arrival", produces = TEXT_XML_VALUE)
    public Flight arrival(
            @RequestParam("id") int id,
            @RequestParam("airport") String airport,
            @RequestParam("fuel") float endFuel,
            @RequestParam("odometer") float endOdometer)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return service.endFlight(id, airport, endFuel, endOdometer);
    }

    @GetMapping(path = "invalidate", produces = TEXT_XML_VALUE)
    public Flight invalidate(@RequestParam("id") int id)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return service.invalidateFlight(id);
    }

    @GetMapping(path = "pirep", produces = TEXT_XML_VALUE)
    public Flight pirep(@RequestParam("id") int id,
            @RequestParam("altitude") float altitude,
            @RequestParam("fuel") float fuel,
            @RequestParam("odometer") float odometer)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return service.updateFlight(id, altitude, fuel, odometer);
    }

    // Additional endpoints

    @PostMapping(path = "flights/", produces = {MediaType.APPLICATION_JSON_VALUE})
    public DurationTotalsAwarePage<Flight> flights(
            @RequestBody(required = false) Flight example,
            @PageableDefault(sort = "startTime", direction = DESC) Pageable pageable
    ) {
        Page<Flight> page;
        page = service.findFlightsByExample(example, pageable);
        return new DurationTotalsAwarePage<>(page.getContent(),
                pageable,
                page.getTotalElements(),
                service.getTotalFlightTimeByExample(example)
        );
    }

    @DeleteMapping(path = "flights/flight/{id}")
    public void deleteFlight(@PathVariable int id) {
        service.deleteFlight(id);
    }

    @GetMapping(path = "flights.json", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Flight> flightsJSON() {
        return service.findAllFlights();
    }

    @GetMapping(path = "flights.xml", produces = {TEXT_XML_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<Flight> flightsXML() {
        return service.findAllFlights();
    }

    @GetMapping(path = "flights.csv", produces = {"text/csv"})
    public String flightsCSV() throws JsonProcessingException {
        final CsvMapper mapper = new CsvMapper();
        final CsvSchema schema = mapper.schemaFor(Flight.class).withHeader();
        return mapper.writer(schema).writeValueAsString(service.findAllFlights());
    }

}
