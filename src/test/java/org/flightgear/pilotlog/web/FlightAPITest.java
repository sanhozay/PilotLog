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

import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.service.FlightService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(FlightServiceController.class)
@SuppressWarnings("javadoc")
public class FlightAPITest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FlightService flightService;

    private Flight flight = new Flight();

    @Before
    public void setUp() {
        when(flightService.beginFlight(
                anyString(), anyString(), anyString(),
                anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat())
        ).thenReturn(flight);
        when(flightService.endFlight(
                anyInt(), anyString(),
                anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat())
        ).thenReturn(flight);
        when(flightService.updateFlight(
                anyInt(), anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyFloat())
        ).thenReturn(flight);
        when(flightService.invalidateFlight(anyInt()))
                .thenReturn(flight);
        when(flightService.findFlightsByExample(any(Flight.class)))
                .thenReturn(Collections.singletonList(flight));
        when(flightService.findFlightsByExample(any(Flight.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(flight)));
    }

    @Test
    public void testDeparture() throws Exception {
        // Given some flight parameters
        String callsign = "G-SHOZ", aircraft = "pup100", airport = "EGCJ";
        float fuel = 20.0f, odometer = 0.0f, altitude = 1000.0f;
        float latitude = 51.0f, longitude = -2.2f;
        // and a departure request
        RequestBuilder request = get("/api/departure")
                .param("callsign", callsign)
                .param("aircraft", aircraft)
                .param("airport", airport)
                .param("altitude", Float.toString(altitude))
                .param("fuel", Float.toString(fuel))
                .param("odometer", Float.toString(odometer))
                .param("latitude", Float.toString(latitude))
                .param("longitude", Float.toString(longitude));
        // when the request is peformed, expect the response to be XML containing an ID
        String idElement = String.format("<id>%d</id>", flight.getId());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_XML))
                .andExpect(content().string(containsString(idElement)));
        // and the flight service to be called with the relevant parameters
        verify(flightService).beginFlight(
                callsign, aircraft, airport, altitude, fuel, odometer, latitude, longitude
        );
    }

    @Test
    public void testArrival() throws Exception {
        // Given some flight parameters
        int id = flight.getId();
        String airport = "EGCJ";
        float fuel = 20.0f, odometer = 0.0f, altitude = 500.0f;
        float latitude = 54.2f, longitude = -1.8f;
        // and an arrival request
        RequestBuilder request = get("/api/arrival")
                .param("id", Integer.toString(id))
                .param("airport", airport)
                .param("altitude", Float.toString(altitude))
                .param("fuel", Float.toString(fuel))
                .param("odometer", Float.toString(odometer))
                .param("latitude", Float.toString(latitude))
                .param("longitude", Float.toString(longitude));
        // when the request is peformed, expect the response to be XML containing an ID
        String idElement = String.format("<id>%d</id>", flight.getId());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_XML))
                .andExpect(content().string(containsString(idElement)));
        // and the flight service to be called with the relevant parameters
        verify(flightService).endFlight(id, airport, altitude, fuel, odometer, latitude, longitude);
    }

    @Test
    public void testInvalidate() throws Exception {
        // Given some flight parameters
        int id = flight.getId();
        // and an invalidate request
        RequestBuilder request = get("/api/invalidate")
                .param("id", Integer.toString(id));
        // when the request is peformed, expect the response to be XML containing an ID
        String idElement = String.format("<id>%d</id>", flight.getId());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_XML))
                .andExpect(content().string(containsString(idElement)));
        // and the flight service to be called with the relevant parameters
        verify(flightService).invalidateFlight(id);
    }

    @Test
    public void testPirep() throws Exception {
        // Given some flight parameters
        int id = flight.getId();
        float fuel = 18.0f, odometer = 10.1f, altitude = 10000;
        float latitude = -30.0f, longitude = -78.2f;
        float heading = 180.0f;
        // and an invalidate request
        RequestBuilder request = get("/api/pirep")
                .param("id", Integer.toString(id))
                .param("altitude", Float.toString(altitude))
                .param("fuel", Float.toString(fuel))
                .param("odometer", Float.toString(odometer))
                .param("latitude", Float.toString(latitude))
                .param("longitude", Float.toString(longitude))
                .param("heading", Float.toString(heading));
        // when the request is peformed, expect the response to be XML containing an ID
        String idElement = String.format("<id>%d</id>", flight.getId());
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_XML))
                .andExpect(content().string(containsString(idElement)));
        // and the flight service to be called with the relevant parameters
        verify(flightService).updateFlight(id, altitude, fuel, odometer, latitude, longitude, heading);
    }

    @Test
    public void testFindFlights() throws Exception {
        // Given a request body
        String origin = "EGNM";
        String json = String.format("{\"origin\":\"%s\"}", origin);
        // and some page parameters
        int page = 0, pageSize = 12;
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "destination");
        // and a paged request for flights
        RequestBuilder request = post("/api/flights/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", Integer.toString(page))
                .param("size", Integer.toString(pageSize))
                .param("sort", String.format("%s,%s", order.getProperty(), order.getDirection()));
        // when the request is peformed, expect the response to be JSON
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        // and a page of flights to be requested from the flights service to provide content
        ArgumentCaptor<Flight> example = ArgumentCaptor.forClass(Flight.class);
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(flightService).findFlightsByExample(example.capture(), pageable.capture());
        assertThat(example.getValue().getOrigin()).isEqualTo(origin);
        assertThat(pageable.getValue().getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getValue().getPageNumber()).isEqualTo(page);
        pageable.getValue().getSort().forEach(sortOrder -> {
            assertThat(sortOrder.getProperty()).isEqualTo(order.getProperty());
            assertThat(sortOrder.getDirection()).isEqualTo(order.getDirection());
        });
    }

    @Test
    public void testDeleteFlight() throws Exception {
        // Given a flight ID
        int id = 10;
        // and a request to delete that flight
        RequestBuilder request = delete(String.format("/api/flights/flight/%d", id));
        // when the request is performed
        mvc.perform(request).andExpect(status().isOk());
        // expect the flight service to be asked to delete that flight
        verify(flightService).deleteFlight(id);
    }

    @Test
    public void testFlightsJSON() throws Exception {
        // Given a request for flights as JSON
        RequestBuilder request = get("/api/flights.json");
        // when the request is performed, expect status OK and a JSON response
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        // and the flight service to be asked for all flights
        verify(flightService).findAllFlights();
    }

    @Test
    public void testFlightsXML() throws Exception {
        // Given a request for flights as XML
        RequestBuilder request = get("/api/flights.xml");
        // when the request is performed, expect status OK and an XML response
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_XML));
        // and the flight service to be asked for all flights
        verify(flightService).findAllFlights();
    }

    @Test
    public void testFlightsCSV() throws Exception {
        // Given a request for flights as XML
        RequestBuilder request = get("/api/flights.csv");
        // when the request is performed, expect status OK and a CSV response
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("text/csv")));
        // and the flight service to be asked for all flights
        verify(flightService).findAllFlights();
    }

}
