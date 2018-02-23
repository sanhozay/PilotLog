package org.flightgear.pilotlog.service;

import org.assertj.core.data.Offset;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    private PageableUtil pageableUtil;

    private FlightService flightService;

    @Before
    public void setUp() {
        flightService = new FlightService(flightRepository);
        flightService.setPageableUtil(pageableUtil);
        when(flightRepository.save(any(Flight.class))).thenAnswer((Answer<Flight>)invocation -> {
            Flight flight = (Flight)invocation.getArguments()[0];
            flight.setId(ID_ACTIVE); // Simulate auto-generate id
            return flight;
        });
        when(flightRepository.findOne(anyInt())).thenAnswer((Answer<Flight>)invocation -> {
            int id = (int)invocation.getArguments()[0];
            if (id == ID_MISSING) {
                // Simulate flight not found
                return null;
            }
            Flight flight = new Flight("G-SHOZ", "707", "EGNM", 1000.0f, 0.0f);
            flight.setId(id);
            // Simulate flight not active
            flight.setStatus(id == ID_COMPLETE ? FlightStatus.COMPLETE : FlightStatus.ACTIVE);
            // Altitude is required for checking altitude updates
            flight.setAltitude(10000);
            return flight;
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
        Flight flight = flightService.beginFlight("G-SHOZ", "707", "EGNM", 1000.0f, 0.0f);
        // expect the repostory to save a flight
        verify(flightRepository).save(any(Flight.class));
        // and the departure properties to be set correctly
        assertThat(flight.getCallsign()).isEqualTo("G-SHOZ");
        assertThat(flight.getAircraft()).isEqualTo("707");
        assertThat(flight.getOrigin()).isEqualTo("EGNM");
        assertThat(flight.getStartFuel()).isEqualTo(1000.0f);
        assertThat(flight.getStartOdometer()).isEqualTo(0.0f);
        // and the status to be active
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.ACTIVE);
        // and the start date to be the current date and time
        assertThat(flight.getStartTime().getTime()).isCloseTo(new Date().getTime(), Offset.offset(500L));
    }

    @Test
    public void testEndFlight() {
        // When ending a flight
        Flight flight = flightService.endFlight(ID_ACTIVE, "EGLL", 900.0f, 100.0f);
        // expect the repository to look for the flight
        verify(flightRepository).findOne(ID_ACTIVE);
        // and the arrival properties to be set correctly
        assertThat(flight.getDestination()).isEqualTo("EGLL");
        assertThat(flight.getEndFuel()).isEqualTo(900.0f);
        assertThat(flight.getEndOdometer()).isEqualTo(100.0f);
        // and the status to be set to complete
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.COMPLETE);
        // and the end date to be the current date and time
        assertThat(flight.getEndTime().getTime()).isCloseTo(new Date().getTime(), Offset.offset(500L));
    }

    @Test(expected = FlightNotFoundException.class)
    public void testEndFlightMissing() {
        // When attempting to end a missing flight
        Flight flight = flightService.endFlight(ID_MISSING, "EGLL", 900.0f, 100.0f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testEndFlightNotActive() {
        // When attempting to end a completed flight
        Flight flight = flightService.endFlight(ID_COMPLETE, "EGLL", 900.0f, 100.0f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_COMPLETE);
    }

    @Test
    public void testEndFlightFuelFreeze() {
        // When ending a flight with the same fuel as it started
        Flight flight = flightService.endFlight(ID_ACTIVE, "EGLL", 1000.0f, 100.0f);
        // expect the repository to look for the flight
        verify(flightRepository).findOne(ID_ACTIVE);
        // and the status to be set to invalid
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.INVALID);
    }

    @Test
    public void testInvalidateFlight() {
        // When invalidating a flight
        Flight flight = flightService.invalidateFlight(ID_ACTIVE);
        // expect the repository to look for the flight
        verify(flightRepository).findOne(ID_ACTIVE);
        // and the status to be set to invalid
        assertThat(flight.getStatus()).isEqualTo(FlightStatus.INVALID);
    }

    @Test(expected = FlightNotFoundException.class)
    public void testInvalidateFlightMissing() {
        // When attempting to invalidate a missing flight
        Flight flight = flightService.invalidateFlight(ID_MISSING);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testInvalidateFlightNotActive() {
        // When attempting to invalidate a flight that is not active
        Flight flight = flightService.invalidateFlight(ID_COMPLETE);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_COMPLETE);
    }

    @Test
    public void testDeleteFlight() {
        // When attempting to delete a completed flight
        flightService.deleteFlight(ID_COMPLETE);
        // expect the repository to look for the flight
        verify(flightRepository).findOne(ID_COMPLETE);
        // and then delete it
        verify(flightRepository).delete(flightRepository.findOne(ID_COMPLETE));
    }

    @Test(expected = FlightNotFoundException.class)
    public void testDeleteFlightMissing() {
        // When attempting to delete a missing flight
        flightService.deleteFlight(ID_MISSING);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testDeleteFlightNotComplete() {
        // When attempting to delete an active flight
        flightService.deleteFlight(ID_ACTIVE);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_ACTIVE);
    }

    @Test
    public void testUpdateFlight() {
        // When updating a flight with an lower altitude than cruise, fuel and odometer
        Flight flight = flightService.updateFlight(ID_ACTIVE, 5000, 900.0f, 100.0f);
        // expect the repository to look for the flight
        verify(flightRepository, times(ID_COMPLETE)).findOne(ID_ACTIVE);
        // and that the altitude is unchanged
        assertThat(flight.getAltitude()).isEqualTo(10000);
        // and that the fuel, odometer and end time are set correctly
        assertThat(flight.getEndFuel()).isEqualTo(900.0f);
        assertThat(flight.getEndOdometer()).isEqualTo(100.0f);
        assertThat(flight.getEndTime().getTime()).isCloseTo(new Date().getTime(), Offset.offset(500L));
    }

    @Test
    public void testUpdateFlightHigherAltitude() {
        // When updating a flight with an higher altitude than cruise
        Flight flight = flightService.updateFlight(ID_ACTIVE, 15000, 900.0f, 100.0f);
        // expect the repository to look for the flight
        verify(flightRepository, times(ID_COMPLETE)).findOne(ID_ACTIVE);
        // and that the altitude is updated to the new altitude
        assertThat(flight.getAltitude()).isEqualTo(15000);
    }

    @Test(expected = FlightNotFoundException.class)
    public void testUpdateFlightMissing() {
        // When attempting to update a missing flight
        Flight flight = flightService.updateFlight(ID_MISSING, 10000, 900.0f, 100.0f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_MISSING);
    }

    @Test(expected = InvalidFlightStatusException.class)
    public void testUpdateFlightNotActive() {
        // When attempting to update a completed flight
        Flight flight = flightService.updateFlight(ID_COMPLETE, 10000, 900.0f, 100.0f);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_COMPLETE);
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
        flightService.purge();
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
        verify(flightRepository).findOne(ID_COMPLETE);
        // and the flight to be found
        assertThat(flight).isNotNull();
    }

    @Test(expected = FlightNotFoundException.class)
    public void testFindFlightByIdMissing() {
        // When searching for a missing flight by ID
        Flight flight = flightService.findFlightById(ID_MISSING);
        // expect the repository to look for the flight and throw an exception
        verify(flightRepository).findOne(ID_MISSING);
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
        Pageable pageable = new PageRequest(0, 10);
        // and a pageable utility class
        PageableUtil util = mock(PageableUtil.class);
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
        Pageable pageable = new PageRequest(0, 10);
        // when finding pageable flights by example
        Page<Flight> flights = flightService.findFlightsByExample(null, pageable);
        // expect the repository to search by pageable only
        verify(flightRepository).findAll(any(Pageable.class));
        // and the pageable utility class to adjust the pageable
        verify(pageableUtil).adjustPageable(pageable, "id", "aircraft");
        // and that the collection returned from the repository is returned by the service
        assertThat(flights).isEqualTo(flightRepository.findAll(any(Pageable.class)));
    }

}
