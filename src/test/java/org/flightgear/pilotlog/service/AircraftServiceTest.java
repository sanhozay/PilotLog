package org.flightgear.pilotlog.service;

import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.domain.AircraftRepository;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SuppressWarnings({"javadoc", "unchecked"})
public class AircraftServiceTest {

    @MockBean
    private AircraftRepository aircraftRepository;

    @MockBean
    private FlightRepository flightRepository;

    @MockBean
    private PageableUtil pageableUtil;

    private AircraftService aircraftService;

    @Before
    public void setUp() {
        aircraftService = new AircraftService(aircraftRepository, flightRepository, pageableUtil);
    }

    @Test
    public void testFindAllAircraft() {
        // When using the service to find all aircraft
        List<Aircraft> aircraft = aircraftService.findAllAircraft();
        // expect the corresponding repository method to be called once
        verify(aircraftRepository, times(1)).findAll();
        // and that the collection returned from the repository is returned by the service
        assertThat(aircraft).isEqualTo(aircraftRepository.findAll());
    }

    @Test
    public void testFindAllAircraftPaged() {
        // Given a pageable
        Pageable pageable = new PageRequest(0, 10);
        // when a page of aircraft is requested
        Page<Aircraft> aircraft = aircraftService.findAllAircraft(pageable);
        // expect the pageable to be adjusted for stability and case
        verify(pageableUtil, times(1)).adjustPageable(pageable, "model", "model");
        // and the call to the repository to be an adjusted pageable
        verify(aircraftRepository, times(0)).findAll(pageable);
        verify(aircraftRepository, times(1)).findAll(any(Pageable.class));
        // and that the page returned from the repository is returned by the service
        assertThat(aircraft).isEqualTo(aircraftRepository.findAll(pageable));
    }

    @Test
    public void testUpdateSummary() {
        // When updating the summary for an aircraft
        aircraftService.updateSummary("707");
        // expect the aircraft summary for that aircraft to be requested from the repository
        verify(flightRepository, times(1)).aircraftSummaryByModel("707");
        // and an aircraft to be saved
        verify(aircraftRepository, times(1)).save(any(Aircraft.class));
    }

}
