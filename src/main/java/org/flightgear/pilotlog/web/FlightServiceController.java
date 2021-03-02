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
import org.flightgear.pilotlog.dto.AirportInfo;
import org.flightgear.pilotlog.dto.FlightDTO;
import org.flightgear.pilotlog.dto.Total;
import org.flightgear.pilotlog.dto.TotalsAwarePage;
import org.flightgear.pilotlog.dto.TrackPointDTO;
import org.flightgear.pilotlog.service.AirportService;
import org.flightgear.pilotlog.service.FlightNotFoundException;
import org.flightgear.pilotlog.service.FlightService;
import org.flightgear.pilotlog.service.InvalidFlightStatusException;
import org.geojson.Feature;
import org.geojson.FeatureCollection;
import org.geojson.LineString;
import org.geojson.LngLatAlt;
import org.geojson.Point;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

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
@SuppressWarnings("WeakerAccess")
public class FlightServiceController {

    private final FlightService flightService;
    private final AirportService airportService;

    public FlightServiceController(FlightService flightService, AirportService airportService) {
        this.airportService = airportService;
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
            @RequestParam("longitude") float longitude,
            @RequestParam("heading") float heading)
            throws FlightNotFoundException, InvalidFlightStatusException {
        return flightService.updateFlight(id, altitude, fuel, odometer, latitude, longitude, heading);
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

    @GetMapping(path = "flights.json", produces = APPLICATION_JSON_VALUE)
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

    @GetMapping(path = "flights/flight/{id}", produces = APPLICATION_JSON_VALUE)
    public Flight flight(@PathVariable int id) {
        return flightService.findFlightById(id);
    }

    @GetMapping(path = "flights/flight/latest", produces = APPLICATION_JSON_VALUE)
    public FlightDTO latestFlight() {
        return flightService.latestFlight();
    }

    @GetMapping(path = "flights/flight/{id}/featurecollection", produces = APPLICATION_JSON_VALUE)
    public FeatureCollection featureCollection(@PathVariable int id) {

        Flight flight = flightService.findFlightById(id);
        if (!flight.isTracked()) {
            return new FeatureCollection();
        }

        List<TrackPointDTO> trackPoints = flightService.getTrackForFlightWithId(id);
        List<LngLatAlt> points = trackPoints.parallelStream().map(trackPoint -> new LngLatAlt(
            trackPoint.getCoordinate().getLongitude(),
            trackPoint.getCoordinate().getLatitude(),
            trackPoint.getAltitude()
        )).collect(Collectors.toList());

        FeatureCollection featureCollection = new FeatureCollection();
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        AirportInfo originInfo = airportService.getAirportInfo(flight.getOrigin());
        Feature origin = new Feature();
        origin.setGeometry(new Point(points.get(0)));
        origin.setProperty("icao", flight.getOrigin());
        origin.setProperty("name", originInfo != null ? originInfo.getName() : "");
        origin.setProperty("type", "O");
        origin.setProperty("date", dateFormat.format(flight.getStartTime()));
        featureCollection.add(origin);

        if (points.size() > 1) {
            Feature track = new Feature();
            LngLatAlt[] p = points.toArray(new LngLatAlt[0]);
            track.setGeometry(new LineString(p));
            featureCollection.add(track);
        }

        if (flight.getDestination() != null) {
            AirportInfo destinationInfo = airportService.getAirportInfo(flight.getDestination());
            Feature destination = new Feature();
            destination.setGeometry(new Point(points.get(points.size() - 1)));
            destination.setProperty("icao", flight.getDestination());
            destination.setProperty("name", destinationInfo != null ? destinationInfo.getName() : "");
            destination.setProperty("type", "D");
            destination.setProperty("date", dateFormat.format(flight.getEndTime()));
            featureCollection.add(destination);
        }

        return featureCollection;
    }

    @GetMapping(path = "flights/flight/{id}/track", produces = APPLICATION_JSON_VALUE)
    public List<TrackPointDTO> flightTrack(@PathVariable int id) {
        Flight flight = flightService.findFlightById(id);
        if (!flight.isTracked()) {
            return new ArrayList<>();
        }
        return flightService.getTrackForFlightWithId(id);
    }

    private Total<Integer> totalOf(ToIntFunction<Flight> function, Page<Flight> page, List<Flight> matches) {
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
