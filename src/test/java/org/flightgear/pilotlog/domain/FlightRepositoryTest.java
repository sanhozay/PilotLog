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

package org.flightgear.pilotlog.domain;

import org.flightgear.pilotlog.integration.FlightRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flightgear.pilotlog.domain.FlightStatus.ACTIVE;
import static org.flightgear.pilotlog.domain.FlightStatus.INVALID;

@ActiveProfiles("test")
@DataJpaTest
@RunWith(SpringRunner.class)
@SuppressWarnings("javadoc")
public class FlightRepositoryTest {

    static final int HOUR = 3600;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FlightRepository flightRepository;

    @Test
    public void testSummaryWithNoFlights() {
        // Given an empty database, expect aircraft summaries to be null
        assertThat(flightRepository.aircraftSummaryByModel("707")).isNull();
    }

    @Test
    public void testSummaryWithOneActiveFlight() {
        // Given a database with a single active flight
        Flight flight = new FlightBuilder("707", "EGNM").status(ACTIVE).build();
        entityManager.persist(flight);
        // expect the aircraft summary for the aircraft to be null
        assertThat(flightRepository.aircraftSummaryByModel(flight.getAircraft())).isNull();
    }

    @Test
    public void testSummaryWithOneInvalidFlight() {
        // Given a database with a single invalid flight
        Flight flight = new FlightBuilder("707", "EGNM").status(INVALID).build();
        entityManager.persist(flight);
        // expect the aircraft summary for the aircraft to be null
        assertThat(flightRepository.aircraftSummaryByModel(flight.getAircraft())).isNull();
    }

    @Test
    public void testSummaryWithOneCompleteFlight() {
        // Given a database with a single completed flight
        Flight flight = new FlightBuilder("707", "EGNM").complete("EGLL", HOUR, 100.0f, 200.0f).build();
        entityManager.persist(flight);
        // expect the aircraft summary to be calculated correctly
        Aircraft summary = flightRepository.aircraftSummaryByModel("707");
        assertThat(summary.getModel()).isEqualTo("707");
        assertThat(summary.getLast().getTime()).isEqualTo(flight.getStartTime().getTime());
        assertThat(summary.getFuelRate().getMin()).isEqualTo(flight.getFuelRate());
        assertThat(summary.getFuelRate().getMax()).isEqualTo(flight.getFuelRate());
        assertThat(summary.getFuelRate().getAvg()).isEqualTo((double)flight.getFuelRate());
        assertThat(summary.getSpeed().getMin()).isEqualTo(flight.getGroundSpeed());
        assertThat(summary.getSpeed().getMax()).isEqualTo(flight.getGroundSpeed());
        assertThat(summary.getSpeed().getAvg()).isEqualTo((double)flight.getGroundSpeed());
        assertThat(summary.getDistance().getMin()).isEqualTo(flight.getDistance());
        assertThat(summary.getDistance().getMax()).isEqualTo(flight.getDistance());
        assertThat(summary.getDistance().getAvg()).isEqualTo((double)flight.getDistance());
        assertThat(summary.getTotalFlights()).isEqualTo(1L);
        assertThat(summary.getTotalDistance()).isEqualTo((double)flight.getDistance());
        assertThat(summary.getTotalFuel()).isEqualTo((double)flight.getFuelUsed());
        assertThat(summary.getTotalDuration()).isEqualTo((long)flight.getDuration());
    }

    @Test
    public void testSummaryWithMultipleFlights() {
        // Given a database with two completed 707 flights
        Flight flight1 = new FlightBuilder("707", "EGNM").complete("EGLL", 2 * HOUR, 170.0f, 110.0f).build();
        Flight flight2 = new FlightBuilder("707", "EGLL").complete("EGGP", HOUR, 100.0f, 100.0f).build();
        // and two tu154b flights, one of which is still active
        Flight flight3 = new FlightBuilder("tu154b", "EGLL").complete("EGPF", HOUR, 100.0f, 100.0f).build();
        Flight flight4 = new FlightBuilder("tu154b", "EGPF").status(ACTIVE).build();

        // Save these flights
        entityManager.persist(flight1);
        entityManager.persist(flight2);
        entityManager.persist(flight3);
        entityManager.persist(flight4);

        // expect the 707 summary to reflect both 707 flights
        Aircraft summary707 = flightRepository.aircraftSummaryByModel("707");
        assertThat(summary707.getModel()).isEqualTo("707");
        assertThat(summary707.getLast().getTime()).isEqualTo(0L);
        assertThat(summary707.getFuelRate().getMin()).isEqualTo(85.0f);
        assertThat(summary707.getFuelRate().getMax()).isEqualTo(100.0f);
        assertThat(summary707.getFuelRate().getAvg()).isEqualTo(90.0);
        assertThat(summary707.getSpeed().getMin()).isEqualTo(55);
        assertThat(summary707.getSpeed().getMax()).isEqualTo(100);
        assertThat(summary707.getSpeed().getAvg()).isEqualTo(70.0);
        assertThat(summary707.getDistance().getMin()).isEqualTo(100.0f);
        assertThat(summary707.getDistance().getMax()).isEqualTo(110.0f);
        assertThat(summary707.getDistance().getAvg()).isEqualTo(105.0);
        assertThat(summary707.getTotalFlights()).isEqualTo(2L);
        assertThat(summary707.getTotalDistance()).isEqualTo(210.0);
        assertThat(summary707.getTotalFuel()).isEqualTo(270.0);
        assertThat(summary707.getTotalDuration()).isEqualTo(3 * 3600L);

        // and the tu154b summary to reflect only the completed flight
        Aircraft summaryTu154 = flightRepository.aircraftSummaryByModel("tu154b");
        assertThat(summaryTu154.getModel()).isEqualTo("tu154b");
        assertThat(summaryTu154.getLast().getTime()).isEqualTo(0L);
        assertThat(summaryTu154.getFuelRate().getMin()).isEqualTo(100.0f);
        assertThat(summaryTu154.getFuelRate().getMax()).isEqualTo(100.0f);
        assertThat(summaryTu154.getFuelRate().getAvg()).isEqualTo(100.0);
        assertThat(summaryTu154.getSpeed().getMin()).isEqualTo(100);
        assertThat(summaryTu154.getSpeed().getMax()).isEqualTo(100);
        assertThat(summaryTu154.getSpeed().getAvg()).isEqualTo(100.0);
        assertThat(summaryTu154.getDistance().getMin()).isEqualTo(100.0f);
        assertThat(summaryTu154.getDistance().getMax()).isEqualTo(100.0f);
        assertThat(summaryTu154.getDistance().getAvg()).isEqualTo(100.0);
        assertThat(summaryTu154.getTotalFlights()).isEqualTo(1L);
        assertThat(summaryTu154.getTotalDistance()).isEqualTo(100.0);
        assertThat(summaryTu154.getTotalFuel()).isEqualTo(100.0);
        assertThat(summaryTu154.getTotalDuration()).isEqualTo(3600L);
    }

}
