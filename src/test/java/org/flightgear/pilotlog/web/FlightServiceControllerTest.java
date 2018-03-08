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
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.domain.TotalsAwarePage;
import org.flightgear.pilotlog.service.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FlightServiceControllerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightServiceController controller;

    private Flight flight;

    @Before
    public void setUp() {
        flight = new Flight("G-SHOZ", "EGCJ", "pup100", 20.0f, 0.0f);
        flight.setStatus(FlightStatus.COMPLETE);
        flight.setDuration(3600);

        when(flightService.beginFlight(
                anyString(), anyString(), anyString(),
                anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat())
        ).thenReturn(new Flight());

        when(flightService.findFlightsByExample(any(Flight.class)))
                .thenReturn(Collections.singletonList(flight));

        when(flightService.findFlightsByExample(any(Flight.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(flight)));

        when(flightService.findAllFlights()).thenReturn(Collections.singletonList(flight));
    }

    @Test
    public void testDeparture() {
        // Given some flight parameters
        String callsign = "G-SHOZ", aircraft = "pup100", airport = "EGCJ";
        float fuel = 20.0f, odometer = 0.0f, altitude = 100.0f;
        float latitude = 51.0f, longitude = 1.0f;
        // When beginning a flight
        Flight flight = controller.departure(
                callsign, aircraft, airport, altitude, fuel, odometer, latitude, longitude
        );
        // expect the value returned from the flight service be returned from the method
        assertThat(flight).isEqualTo(flightService.beginFlight(
                callsign, aircraft, airport, altitude, fuel, odometer, latitude, longitude
        ));
    }

    @Test
    public void testArrival() {
        // Given some flight parameters
        int id = 100;
        String airport = "EGCJ";
        float fuel = 20.0f, odometer = 0.0f, altitude = 100.0f;
        float latitude = 51.0f, longitude = 1.0f;
        // When ending a flight
        Flight flight = controller.arrival(id, airport, altitude, fuel, odometer, latitude, longitude);
        // expect the value returned from the flight service be returned from the method
        assertThat(flight).isEqualTo(flightService.endFlight(
                id, airport, altitude, fuel, odometer, latitude, longitude
        ));
    }

    @Test
    public void testInvalidate() {
        // Given some flight parameters
        int id = 100;
        // When invalidating a flight
        Flight flight = controller.invalidate(id);
        // expect the value returned from the flight service be returned from the method
        assertThat(flight).isEqualTo(flightService.invalidateFlight(id));
    }

    @Test
    public void testPirep() {
        // Given some flight parameters
        int id = 100;
        float altitude = 10000, fuel = 18.0f, odometer = 10.0f;
        float latitude = 51.0f, longitude = 1.0f;
        // When invalidating a flight
        Flight flight = controller.pirep(id, altitude, fuel, odometer, latitude, longitude);
        // expect the value returned from the flight service be returned from the method
        assertThat(flight).isEqualTo(flightService.updateFlight(
                id, altitude, fuel, odometer, latitude, longitude
        ));
    }

    @Test
    public void testFlightsGetRequest() {
        // Given a pageable instance and an example flight
        Pageable pageable = new PageRequest(0, 10);
        Flight example = new Flight();
        // when getting a page of flights
        TotalsAwarePage<Flight> page = controller.flights(example, pageable);
        // expect all flights to be requested from the flights service
        verify(flightService).findFlightsByExample(example);
        // and a page of flights to be requested from the flights service
        verify(flightService).findFlightsByExample(example, pageable);
        // and the resulting page to have the correct content
        assertThat(page.getContent()).isEqualTo(flightService.findFlightsByExample(example, pageable).getContent());
        // and the original pageable
        assertThat(page.getSize()).isEqualTo(pageable.getPageSize());
        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        // and the correct number of elements
        assertThat(page.getTotalElements()).isEqualTo(flightService.findFlightsByExample(example).size());
        // and totals for distance, duration, flights and fuel
        assertThat(page.getTotals().get("duration").getTotal()).isEqualTo(flight.getDuration());
    }

    @Test
    public void testFlightsJSON() {
        // When requesting flights as JSON
        List<Flight> flights = controller.flightsJSON();
        // expect the method to return all flights
        assertThat(flights).isEqualTo(flightService.findAllFlights());
    }

    @Test
    public void testFlightsXML() {
        // When requesting flights as XML
        List<Flight> flights = controller.flightsXML();
        // and the method to return all flights
        assertThat(flights).isEqualTo(flightService.findAllFlights());
    }

    @Test
    public void testFlightsCSV() throws JsonProcessingException {
        // When requesting flights as CSV
        String csv = controller.flightsCSV();
        // expect the method to return all flights in CSV format
        assertThat(csv.replaceAll("[\r\n\t ]+", "")).isEqualTo("id,callsign,aircraft,origin,startTime," +
                "startFuel,startOdometer,destination,endTime,endFuel,endOdometer," +
                "fuelUsed,fuelRate,distance,groundSpeed,duration,status,altitude,complete,reserve" +
                "0,G-SHOZ,EGCJ,pup100,,20.0,0.0,,,,,,,,,3600,COMPLETE,,true,");
    }

}
