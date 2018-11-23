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

import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.domain.AircraftRepository;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"javadoc", "unchecked"})
public class AircraftServiceTest {

    @Mock
    private AircraftRepository aircraftRepository;

    @Mock
    private FlightRepository flightRepository;

    @Mock
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
        verify(aircraftRepository).findAll();
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
        verify(pageableUtil).adjustPageable(pageable, "model", "model");
        // and the call to the repository to be an adjusted pageable
        verify(aircraftRepository, never()).findAll(pageable);
        verify(aircraftRepository).findAll(any(Pageable.class));
        // and that the page returned from the repository is returned by the service
        assertThat(aircraft).isEqualTo(aircraftRepository.findAll(pageable));
    }

    @Test
    public void testUpdateSummary() {
        // Given an aircraft with a non-null summary
        String aircraft = "707";
        given(flightRepository.aircraftSummaryByModel(aircraft)).willReturn(new Aircraft());
        // when updating the summary for an aircraft
        aircraftService.updateSummary(aircraft);
        // expect the aircraft summary for that aircraft to be requested from the repository
        verify(flightRepository).aircraftSummaryByModel(aircraft);
        // and an aircraft to be saved
        verify(aircraftRepository).save(any(Aircraft.class));
    }

    @Test
    public void testUpdateSummaryAircraftNull() {
        // Given an aircraft with a null summary
        String aircraft = "Bleriot-XI";
        given(flightRepository.aircraftSummaryByModel(aircraft)).willReturn(null);
        // when updating the summary for an aircraft
        aircraftService.updateSummary(aircraft);
        // expect the aircraft summary for that aircraft to be requested from the repository
        verify(flightRepository).aircraftSummaryByModel(aircraft);
        // but the aircraft repository not to attempt to save the null aircraft
        verify(aircraftRepository, never()).save(any(Aircraft.class));
        // it should delete the summary instead
        // verify(aircraftRepository).delete(any(Aircraft.class));
    }

}
