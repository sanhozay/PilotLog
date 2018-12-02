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

import org.flightgear.pilotlog.domain.Aircraft;
import org.flightgear.pilotlog.domain.TotalsAwarePage;
import org.flightgear.pilotlog.service.AircraftService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("javadoc")
public class AircraftServiceControllerTest {

    @Mock
    private AircraftService aircraftService;

    @InjectMocks
    private AircraftServiceController controller;

    private Aircraft aircraft;

    @Before
    public void setUp() {
        aircraft = new Aircraft("pup100");
        aircraft.setTotalDistance(100.0);
        aircraft.setTotalDuration(3600L);
        aircraft.setTotalFlights(1L);
        aircraft.setTotalFuel(10.0);

        List<Aircraft> content = Collections.singletonList(aircraft);
        when(aircraftService.findAllAircraft())
                .thenReturn(content);
        when(aircraftService.findAllAircraft(any(Pageable.class)))
                .thenReturn(new PageImpl<>(content));

        when(aircraftService.getTotalDistance()).thenReturn(aircraft.getTotalDistance());
        when(aircraftService.getTotalDuration()).thenReturn(aircraft.getTotalDuration());
        when(aircraftService.getTotalFuel()).thenReturn(aircraft.getTotalFuel());
        when(aircraftService.getTotalFlights()).thenReturn(aircraft.getTotalFlights());
    }

    @Test
    public void testAircraftGetRequest() {
        // Given a pageable instance
        Pageable pageable = PageRequest.of(0, 10);
        // when getting an aircraft summary
        TotalsAwarePage<Aircraft> page = controller.aircraft(pageable);
        // and totals to be requested for distance, fuel, flights and duration
        verify(aircraftService).getTotalDistance();
        verify(aircraftService).getTotalFuel();
        verify(aircraftService).getTotalFlights();
        verify(aircraftService).getTotalDuration();
        // and a page of aircraft to be requested from the aircraft service
        verify(aircraftService).findAllAircraft(pageable);
        // and the resulting page to have the correct content
        assertThat(page.getContent()).isEqualTo(aircraftService.findAllAircraft(pageable).getContent());
        // and the original pageable
        assertThat(page.getSize()).isEqualTo(pageable.getPageSize());
        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());
        // and the correct number of elements
        assertThat(page.getTotalElements()).isEqualTo(aircraftService.findAllAircraft().size());
        // and totals for distance, duration, flights and fuel
        assertThat(page.getTotals().get("distance").getTotal()).isEqualTo(aircraft.getTotalDistance());
        assertThat(page.getTotals().get("duration").getTotal()).isEqualTo(aircraft.getTotalDuration());
        assertThat(page.getTotals().get("flights").getTotal()).isEqualTo(aircraft.getTotalFlights());
        assertThat(page.getTotals().get("fuel").getTotal()).isEqualTo(aircraft.getTotalFuel());
    }
}
