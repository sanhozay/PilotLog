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
import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.domain.Total;
import org.flightgear.pilotlog.domain.TotalsAwarePage;
import org.flightgear.pilotlog.service.AircraftService;
import org.flightgear.pilotlog.service.FlightService;
import org.flightgear.pilotlog.service.exceptions.FlightNotFoundException;
import org.flightgear.pilotlog.service.exceptions.InvalidFlightStatusException;
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

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
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
    private FlightService flightService;

    @Autowired
    private AircraftService aircraftService;

    // Flightgear endpoints

    @GetMapping(path = "departure", produces = TEXT_XML_VALUE)
    public Flight departure(
            @RequestParam("callsign") String callsign,
            @RequestParam("aircraft") String aircraft,
            @RequestParam("airport") String airport,
            @RequestParam("fuel") float startFuel,
            @RequestParam("odometer") float startOdometer) {
        return flightService.beginFlight(callsign, aircraft, airport, startFuel, startOdometer);
    }

    @GetMapping(path = "arrival", produces = TEXT_XML_VALUE)
    public Flight arrival(
            @RequestParam("id") int id,
            @RequestParam("airport") String airport,
            @RequestParam("fuel") float endFuel,
            @RequestParam("odometer") float endOdometer)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return flightService.endFlight(id, airport, endFuel, endOdometer);
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
            @RequestParam("odometer") float odometer)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return flightService.updateFlight(id, altitude, fuel, odometer);
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
        totals.put("duration", getFlightDurationTotal(page, matches));
        return new TotalsAwarePage<>(
                page.getContent(),
                pageable,
                page.getTotalElements(),
                totals
        );
    }

    private Total<Integer> getFlightDurationTotal(Page<Flight> page, List<Flight> matches) {
        int pageTotal = page.getContent().parallelStream()
                .filter(flight -> flight.getStatus() == FlightStatus.COMPLETE)
                .mapToInt(Flight::getDuration)
                .sum();
        int grandTotal = matches.parallelStream()
                .filter(flight -> flight.getStatus() == FlightStatus.COMPLETE)
                .mapToInt(Flight::getDuration)
                .sum();
        return new Total<>(pageTotal, grandTotal);
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

    @GetMapping(path = "aircraft/", produces = APPLICATION_JSON_VALUE)
    public TotalsAwarePage<Aircraft> aircraft(
            @PageableDefault(sort = "totalFlights", direction = DESC) Pageable pageable
    ) {
        List<Aircraft> all = aircraftService.findAllAircraft();
        Page<Aircraft> page = aircraftService.findAllAircraft(pageable);
        Map<String, Total> totals = new HashMap<>();
        totals.put("duration", getAircraftDurationTotal(all, page));
        totals.put("fuel", getAircraftFuelTotal(all, page));
        totals.put("distance", getAircraftDistanceTotal(all, page));
        totals.put("flights", getAircraftFlightsTotal(all, page));
        return new TotalsAwarePage<>(
                page.getContent(),
                pageable,
                page.getTotalElements(),
                totals
        );
    }

    // TODO: Refactor these methods into something more generic

    private Total<Long> getAircraftDurationTotal(List<Aircraft> all, Page<Aircraft> page) {
        long grandTotal = all.parallelStream()
                .mapToLong(Aircraft::getTotalDuration)
                .sum();
        long pageTotal = page.getContent().parallelStream()
                .mapToLong(Aircraft::getTotalDuration)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

    private Total<Long> getAircraftFlightsTotal(List<Aircraft> all, Page<Aircraft> page) {
        long grandTotal = all.parallelStream()
                .mapToLong(Aircraft::getTotalFlights)
                .sum();
        long pageTotal = page.getContent().parallelStream()
                .mapToLong(Aircraft::getTotalFlights)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

    private Total<Double> getAircraftFuelTotal(List<Aircraft> all, Page<Aircraft> page) {
        double grandTotal = all.parallelStream()
                .mapToDouble(Aircraft::getTotalFuel)
                .sum();
        double pageTotal = page.getContent().parallelStream()
                .mapToDouble(Aircraft::getTotalFuel)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

    private Total<Double> getAircraftDistanceTotal(List<Aircraft> all, Page<Aircraft> page) {
        double grandTotal = all.parallelStream()
                .mapToDouble(Aircraft::getTotalDistance)
                .sum();
        double pageTotal = page.getContent().parallelStream()
                .mapToDouble(Aircraft::getTotalDistance)
                .sum();
        return new Total<>(pageTotal, grandTotal);
    }

}
