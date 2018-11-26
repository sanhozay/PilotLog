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

import org.assertj.core.data.Offset;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.flightgear.pilotlog.domain.TrackPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"javadoc", "unchecked"})
public class FlightServiceTest {

    // IDs to simulate flights with differing status
    private static final int ID_MISSING = 0;
    private static final int ID_COMPLETE = 1;
    private static final int ID_ACTIVE = 100;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private AircraftService aircraftService;

    @Mock
    private PageableUtil pageableUtil;

    private FlightService flightService;

    @Before
    public void setUp() {
        flightService = spy(new FlightService(flightRepository, aircraftService));
        flightService.setPageableUtil(pageableUtil);
        when(flightRepository.save(any(Flight.class))).thenAnswer((Answer<Flight>)invocation -> {
            Flight flight = (Flight)invocation.getArguments()[0];
            flight.setId(ID_ACTIVE); // Simulate auto-generate id
            return flight;
        });
        when(flightRepository.findById(anyInt())).thenAnswer((Answer<Optional<Flight>>)invocation -> {
            int id = (int)invocation.getArguments()[0];
            if (id == ID_MISSING) {
                // Simulate flight not found
                return Optional.empty();
            }
            Flight flight = new Flight("G-SHOZ", "707", "EGNM", 1000.0f, 0.0f);
            flight.setId(id);
            // Simulate flight not active
            flight.setStatus(id == ID_COMPLETE ? FlightStatus.COMPLETE : FlightStatus.ACTIVE);
            // Altitude is required for checking altitude updates
            flight.setAltitude(10000);
            return Optional.of(flight);
        });
        when(flightRepository.findByStatus(any(FlightStatus.class))).thenAnswer((Answer<Set<Flight>>)invocation -> {
            Flight flight = new Flight("G-SHOZ", "707", "EGNM", 1000.0f, 0.0f);
            flight.setStartTime(new Date(new Random().nextInt()));
            flight.setStatus((FlightStatus)invocation.getArguments()[0]);
            return Collections.singleton(flight);
        });
    }

    @Test
    public void testBeginFlight() {
        // When beginning a new flight
        Flight flight = flightService.beginFlight(
                "G-SHOZ", "707", "EGNM", 640.0f, 1000.0f, 0.0f, 53.0f, -1.0f
        );
        long now = new Date().getTime();
        // expect old flights to be purged
        verify(flightService).purgeIncompleteFlights();
        // expect the repostory to save a flight
        verify(flightRepository).save(any(Flight.class));
        // and the departure properties to be set correctly
        assertThat(flight.getCallsign()).isEqualTo("G-SHOZ");
        assertThat(flight.getAircraft()).isEqualTo("707");
        assertThat(flight.getOrigin()).isEqualTo("EGNM");
        assertThat(flight.getStartFuel()).isEqualTo(1000.0f);
        assertThat(flight.getStartOdometer()).isEqualTo(0.0f);
        // and the flight to contain a track point for the origin
        assertThat(flight.getTrack().size()).isEqualTo(1);
        TrackPoint trackPoint = flight.getTrack().get(0);
        assertThat(trackPoint.getAltitude()).isEqualTo(640.0f);
        assertThat(trackPoint.getCoordinate().getLatitude()).isEqualTo(53.0f);
        assertThat(trackPoint.getCoordinate().getLongitude()).isEqualTo(-1.0f);
        assertThat(trackPoint.getFuel()).isEqualTo(1000.0f);
        assertThat(trackPoint.getOdometer()).isEqualTo(0.0f);
        assertThat(trackPoint.getTimestamp().getTime()).isCloseTo(now, Offset.offset(500L));
        // and the status to be active
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.ACTIVE);
        // and the start date to be the current date and time
        assertThat(flight.getStartTime().getTime()).isCloseTo(now, Offset.offset(500L));
    }

    @Test
    public void testEndFlight() {
        // When ending a flight
        Flight flight = flightService.endFlight(ID_ACTIVE, "EGLL", 200.0f, 900.0f, 100.0f, 51.0f, -0.2f);
        long now = new Date().getTime();
        // expect the repository to look for the flight
        verify(flightRepository).findById(ID_ACTIVE);
        // and the arrival properties to be set correctly
        assertThat(flight.getDestination()).isEqualTo("EGLL");
        assertThat(flight.getEndFuel()).isEqualTo(900.0f);
        assertThat(flight.getEndOdometer()).isEqualTo(100.0f);
        // and the flight to contain a track point for the destination
        assertThat(flight.getTrack().size()).isGreaterThan(0);
        TrackPoint trackPoint = flight.getTrack().get(flight.getTrack().size() - 1);
        assertThat(trackPoint.getAltitude()).isEqualTo(200.0f);
        assertThat(trackPoint.getCoordinate().getLatitude()).isEqualTo(51.0f);
        assertThat(trackPoint.getCoordinate().getLongitude()).isEqualTo(-0.2f);
        assertThat(trackPoint.getFuel()).isEqualTo(900.0f);
        assertThat(trackPoint.getOdometer()).isEqualTo(100.0f);
        assertThat(trackPoint.getTimestamp().getTime()).isCloseTo(now, Offset.offset(500L));
        // and the status to be set to complete
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.COMPLETE);
        // and the end date to be the current date and time
        assertThat(flight.getEndTime().getTime()).isCloseTo(new Date().getTime(), Offset.offset(500L));
        // and the flight service to update completed flights
        verify(flightService).updateComputedFields(flight);
        // and the aircraft service to update the summary for the aircraft
        verify(aircraftService).updateSummary(flight.getAircraft());
    }

    @Test(expected = FlightNotFoundException.class)
    public void testEndFlightMissing() {
        // When attempting to end a missing flight
        Flight flight = flightService.endFlight(ID_MISSING, "EGLL", 0.0f, 900.0f, 100.0f, 51.0f, -0.2f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testEndFlightNotActive() {
        // When attempting to end a completed flight
        Flight flight = flightService.endFlight(ID_COMPLETE, "EGLL", 0.0f, 900.0f, 100.0f, 51.0f, -0.2f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_COMPLETE);
    }

    @Test
    public void testEndFlightFuelFreeze() {
        // When ending a flight with the same fuel as it started
        Flight flight = flightService.endFlight(ID_ACTIVE, "EGLL", 0.0f, 1000.0f, 100.0f, 51.0f, -0.2f);
        // expect the repository to look for the flight
        verify(flightRepository).findById(ID_ACTIVE);
        // and the status to be set to invalid
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.INVALID);
    }

    @Test
    public void testInvalidateFlight() {
        // When invalidating a flight
        Flight flight = flightService.invalidateFlight(ID_ACTIVE);
        // expect the repository to look for the flight
        verify(flightRepository).findById(ID_ACTIVE);
        // and the status to be set to invalid
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.INVALID);
    }

    @Test(expected = FlightNotFoundException.class)
    public void testInvalidateFlightMissing() {
        // When attempting to invalidate a missing flight
        Flight flight = flightService.invalidateFlight(ID_MISSING);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testInvalidateFlightNotActive() {
        // When attempting to invalidate a flight that is not active
        Flight flight = flightService.invalidateFlight(ID_COMPLETE);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_COMPLETE);
    }

    @Test
    public void testDeleteFlight() {
        // When attempting to delete a completed flight
        flightService.deleteFlight(ID_COMPLETE);
        // expect the repository to look for the flight
        verify(flightRepository).findById(ID_COMPLETE);
        // and then delete it
        Optional<Flight> optional = flightRepository.findById(ID_COMPLETE);
        assertThat(optional.isPresent());
        verify(flightRepository).delete(optional.get());
        // and update the summary for the aircraft
        verify(aircraftService).updateSummary(optional.get().getAircraft());
    }

    @Test(expected = FlightNotFoundException.class)
    public void testDeleteFlightMissing() {
        // When attempting to delete a missing flight
        flightService.deleteFlight(ID_MISSING);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testDeleteFlightNotComplete() {
        // When attempting to delete an active flight
        flightService.deleteFlight(ID_ACTIVE);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_ACTIVE);
    }

    @Test
    public void testUpdateFlight() {
        // When updating a flight with an lower altitude than cruise, fuel and odometer
        Flight flight = flightService.updateFlight(ID_ACTIVE, 5000.0f, 900.0f, 100.0f, 53.0f, 1.0f, 0.0f);
        long now = new Date().getTime();
        // expect the repository to look for the flight
        verify(flightRepository, times(ID_COMPLETE)).findById(ID_ACTIVE);
        // and that the altitude is unchanged
        assertThat(flight.getAltitude()).isEqualTo(10000);
        // and the flight to contain a track point for the pilot report
        assertThat(flight.getTrack().size()).isGreaterThan(0);
        TrackPoint trackPoint = flight.getTrack().get(flight.getTrack().size() - 1);
        assertThat(trackPoint.getAltitude()).isEqualTo(5000.0f);
        assertThat(trackPoint.getCoordinate().getLatitude()).isEqualTo(53.0f);
        assertThat(trackPoint.getCoordinate().getLongitude()).isEqualTo(1.0f);
        assertThat(trackPoint.getFuel()).isEqualTo(900.0f);
        assertThat(trackPoint.getOdometer()).isEqualTo(100.0f);
        assertThat(trackPoint.getTimestamp().getTime()).isCloseTo(now, Offset.offset(500L));
        // and that the fuel, odometer and end time are set correctly
        assertThat(flight.getEndFuel()).isEqualTo(900.0f);
        assertThat(flight.getEndOdometer()).isEqualTo(100.0f);
        assertThat(flight.getEndTime().getTime()).isCloseTo(new Date().getTime(), Offset.offset(500L));
        // and the flight service to update completed flights
        verify(flightService).updateComputedFields(flight);
    }

    @Test
    public void testUpdateFlightHigherAltitude() {
        // When updating a flight with an higher altitude than cruise
        Flight flight = flightService.updateFlight(ID_ACTIVE, 15000, 900.0f, 100.0f, 51.0f, 1.0f, 0.0f);
        // expect the repository to look for the flight
        verify(flightRepository, times(ID_COMPLETE)).findById(ID_ACTIVE);
        // and the flight to contain a track point with the real altitude
        assertThat(flight.getTrack().size()).isGreaterThan(0);
        TrackPoint trackPoint = flight.getTrack().get(flight.getTrack().size() - 1);
        assertThat(trackPoint.getAltitude()).isEqualTo(15000.0f);
        // and that the altitude is updated to the new altitude
        assertThat(flight.getAltitude()).isEqualTo(15000);
        // and the flight service to update completed flights
        verify(flightService).updateComputedFields(flight);
    }

    @Test(expected = FlightNotFoundException.class)
    public void testUpdateFlightMissing() {
        // When attempting to update a missing flight
        Flight flight = flightService.updateFlight(ID_MISSING, 10000, 900.0f, 100.0f, 51.0f, 1.0f, 0.0f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testUpdateFlightNotActive() {
        // When attempting to update a completed flight
        Flight flight = flightService.updateFlight(ID_COMPLETE, 10000, 900.0f, 100.0f, 51.0f, 1.0f, 0.0f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_COMPLETE);
    }

    @Test
    public void testComputedFieldsNew() {
        // Given a new flight
        Flight flight = new Flight();
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect all the computed fields to be null
        assertThat(flight.getDuration()).isNull();
        assertThat(flight.getFuelUsed()).isNull();
        assertThat(flight.getFuelRate()).isNull();
        assertThat(flight.getReserve()).isNull();
        assertThat(flight.getDistance()).isNull();
        assertThat(flight.getGroundSpeed()).isNull();
    }

    @Test
    public void testComputedFieldsDuration() {
        // Given a flight with a start and end time
        Flight flight = new Flight();
        flight.setStartTime(new Date(0L));
        flight.setEndTime(new Date(3600 * 1000L));
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect the duration to be set correctly
        assertThat(flight.getDuration()).isEqualTo(3600);
    }

    @Test
    public void testComputedFieldsZeroDuration() {
        // Given a completed flight with the same start and end time
        Flight flight = new Flight();
        flight.setStartTime(new Date(0L));
        flight.setEndTime(new Date(0L));
        flight.setStatus(FlightStatus.COMPLETE);
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect the flight to be invalidated
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.INVALID);
    }

    @Test
    public void testComputedFieldsFuelUsed() {
        // Given a completed flight with start and end fuel
        Flight flight = new Flight();
        flight.setStartFuel(100.0f);
        flight.setEndFuel(90.0f);
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect the fuel usage to be set correctly
        assertThat(flight.getFuelUsed()).isEqualTo(10.0f);
    }

    @Test
    public void testComputedFieldsFuelRateAndReserve() {
        // Given a completed flight with start and end fuel, and start and end time
        Flight flight = new Flight();
        flight.setStartFuel(100.0f);
        flight.setEndFuel(50.0f);
        flight.setStartTime(new Date(0L));
        flight.setEndTime(new Date(3600 * 1000L));
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect the fuel rate and reserve to be set correctly
        assertThat(flight.getFuelRate()).isEqualTo(50.0f);
        assertThat(flight.getReserve()).isEqualTo(3600);
    }

    @Test
    public void testComputedFieldsDistance() {
        // Given a completed flight with start and end odometer
        Flight flight = new Flight();
        flight.setStartOdometer(0.0f);
        flight.setEndOdometer(100.0f);
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect the distance to be set correctly
        assertThat(flight.getDistance()).isEqualTo(100.0f);
    }

    @Test
    public void testComputedFieldsGroundSpeed() {
        // Given a completed flight with start and end odometer, and start and end times
        Flight flight = new Flight();
        flight.setStartOdometer(0.0f);
        flight.setEndOdometer(100.0f);
        flight.setStartTime(new Date(0L));
        flight.setEndTime(new Date(3600 * 1000L));
        // when updating the computed fields
        flightService.updateComputedFields(flight);
        // expect the ground speed to be set correctly
        assertThat(flight.getGroundSpeed()).isEqualTo(100);
    }

    @Test
    public void testPurge() {
        // When purging invalid and active flights
        flightService.purgeIncompleteFlights();
        // expect the repository not to search for new and completed flights
        verify(flightRepository, never()).findByStatus(FlightStatus.NEW);
        verify(flightRepository, never()).findByStatus(FlightStatus.COMPLETE);
        // and to search for active and invalid flights
        verify(flightRepository).findByStatus(FlightStatus.ACTIVE);
        verify(flightRepository).findByStatus(FlightStatus.INVALID);
        // and to delete the active and invalid flights
        verify(flightRepository, times(2)).delete(any(Flight.class));
    }

    @Test
    public void testFindFlightById() {
        // When searching for an existing flight by ID
        Flight flight = flightService.findFlightById(ID_COMPLETE);
        // expect the repository to look for the flight
        verify(flightRepository).findById(ID_COMPLETE);
        // and the flight to be found
        assertThat(flight).isNotNull();
    }

    @Test(expected = FlightNotFoundException.class)
    public void testFindFlightByIdMissing() {
        // When searching for a missing flight by ID
        Flight flight = flightService.findFlightById(ID_MISSING);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findById(ID_MISSING);
    }

    @Test
    public void testFindAllFlights() {
        // When searching for all flights
        List<Flight> flights = flightService.findAllFlights();
        // expect the repository to look for all flights
        verify(flightRepository).findAll();
        // and that the collection returned from the repository is returned by the service
        assertThat(flights).isEqualTo(flightRepository.findAll());
    }

    @Test
    public void testFindAllFlightsByExample() {
        // Given an example flight
        Flight flight = new Flight();
        // when searching for all flights by example
        List<Flight> flights = flightService.findFlightsByExample(flight);
        // expect the repository to find flights by example
        verify(flightRepository).findAll(any(Example.class));
        // and that the collection returned from the repository is returned by the service
        assertThat(flights).isEqualTo(flightRepository.findAll(any(Example.class)));
    }

    @Test
    public void testFindAllFlightsByNullExample() {
        // When searching for all flights with a null example
        List<Flight> flights = flightService.findFlightsByExample(null);
        // expect the repository to find all flights
        verify(flightRepository).findAll();
        // and that the collection returned from the repository is returned by the service
        assertThat(flights).isEqualTo(flightRepository.findAll());
    }

    @Test
    public void testFindFlightsByExample() {
        // Given an example flight
        Flight flight = new Flight();
        // and a pageable
        Pageable pageable = PageRequest.of(0, 10);
        // and a pageable utility class
        given(pageableUtil.adjustPageable(pageable, "id", "aircraft"))
            .willReturn(PageRequest.of(0, 10, Sort.unsorted()));
        // when finding pageable flights by example
        Page<Flight> flights = flightService.findFlightsByExample(flight, pageable);
        // expect the repository to search by example and pageable
        verify(flightRepository).findAll(any(Example.class), any(Pageable.class));
        // and the pageable utility class to adjust the pageable
        verify(pageableUtil).adjustPageable(pageable, "id", "aircraft");
        // and that the collection returned from the repository is returned by the service
        assertThat(flights).isEqualTo(flightRepository.findAll(any(Example.class), any(Pageable.class)));
    }

    @Test
    public void testFindFlightsByNullExample() {
        // Given a pageable
        Pageable pageable = PageRequest.of(0, 10);
        // and a pageable util
        given(pageableUtil.adjustPageable(pageable, "id", "aircraft"))
            .willReturn(PageRequest.of(0, 10, Sort.unsorted()));
        // when finding pageable flights by example
        Page<Flight> flights = flightService.findFlightsByExample(null, pageable);
        // expect the repository to search by pageable only
        verify(flightRepository).findAll(pageable);
        // and the pageable utility class to adjust the pageable
        verify(pageableUtil).adjustPageable(pageable, "id", "aircraft");
        // and that the collection returned from the repository is returned by the service
        assertThat(flights).isEqualTo(flightRepository.findAll(pageable));
    }

}
