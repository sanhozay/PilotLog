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
import org.flightgear.pilotlog.service.AircraftService;
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

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(AircraftServiceController.class)
public class AircraftAPITest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AircraftService aircraftService;

    private Flight flight = new Flight();

    @Before
    public void setUp() {
        when(aircraftService.findAllAircraft())
                .thenReturn(new ArrayList<>());
        when(aircraftService.findAllAircraft(any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
    }

    @Test
    public void testFindAircraft() throws Exception {
        // Given some page parameters
        int page = 0, pageSize = 8;
        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "aircraft");
        // and a paged request for flights
        RequestBuilder request = get("/api/aircraft/")
                .param("page", Integer.toString(page))
                .param("size", Integer.toString(pageSize))
                .param("sort", String.format("%s,%s", order.getProperty(), order.getDirection()));
        // when the request is peformed, expect the response to be JSON
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        // and all aircraft to be requested from the aircraft service to calculate totals
        verify(aircraftService).findAllAircraft();
        // and a page of aircraft to be requested from the aircraft service to provide content
        ArgumentCaptor<Pageable> pageable = ArgumentCaptor.forClass(Pageable.class);
        verify(aircraftService).findAllAircraft(pageable.capture());
        assertThat(pageable.getValue().getPageSize()).isEqualTo(pageSize);
        assertThat(pageable.getValue().getPageNumber()).isEqualTo(page);
        pageable.getValue().getSort().forEach(sortOrder -> {
            assertThat(sortOrder.getProperty()).isEqualTo(order.getProperty());
            assertThat(sortOrder.getDirection()).isEqualTo(order.getDirection());
        });
    }

}
