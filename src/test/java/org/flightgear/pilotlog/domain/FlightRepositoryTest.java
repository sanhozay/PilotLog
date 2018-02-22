package org.flightgear.pilotlog.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@RunWith(SpringRunner.class)
@SuppressWarnings("javadoc")
public class FlightRepositoryTest {

    private static final int HOUR = 3600;

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
        Flight flight = createIncompleteFlight("707", "EGNM", FlightStatus.ACTIVE);
        // expect the aircraft summary for the aircraft to be null
        assertThat(flightRepository.aircraftSummaryByModel(flight.getAircraft())).isNull();
    }

    @Test
    public void testSummaryWithOneInvalidFlight() {
        // Given a database with a single invalid flight
        Flight flight = createIncompleteFlight("707", "EGNM", FlightStatus.INVALID);
        // expect the aircraft summary for the aircraft to be null
        assertThat(flightRepository.aircraftSummaryByModel(flight.getAircraft())).isNull();
    }

    @Test
    public void testSummaryWithOneCompleteFlight() {
        // Given a database with a single completed flight
        Flight flight = createCompletedFlight("707", "EGNM", "EGLL", HOUR, 100.0f, 200.0f);
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
        // Given a database with two 707 flights
        Flight flight1 = createCompletedFlight("707", "EGNM", "EGLL", 2 * HOUR, 170.0f, 110.0f);
        Flight flight2 = createCompletedFlight("707", "EGNM", "EGLL", HOUR, 100.0f, 100.0f);
        // and two tu154b flights, one of which is still active
        Flight flight3 = createCompletedFlight("tu154b", "EGLL", "EGPF", HOUR, 100.0f, 100.0f);
        Flight flight4 = createIncompleteFlight("tu154b", "EGPF", FlightStatus.ACTIVE);

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

    // Flight creation helpers

    private Flight createIncompleteFlight(String aircraft, String origin, FlightStatus status) {
        Flight flight = new Flight("G-SHOZ", aircraft, origin, 1000.0f, 0.0f);
        flight.setStartTime(new Date(0L));
        flight.setStatus(status);
        return entityManager.persist(flight);
    }

    private Flight createCompletedFlight(
            String aircraft, String origin, String destination,
            int duration, float fuelUsed, float distance
    ) {
        Flight flight = new Flight("G-SHOZ", aircraft, origin, 1000.0f, 0.0f);
        flight.setStartTime(new Date(0L));
        flight.setStatus(FlightStatus.COMPLETE);
        flight.setAltitude(10000);
        flight.setEndOdometer(flight.getStartOdometer() + distance);
        flight.setDistance(distance);
        flight.setGroundSpeed((int)(HOUR * distance / duration));
        flight.setEndFuel(flight.getStartFuel() - fuelUsed);
        flight.setFuelUsed(fuelUsed);
        flight.setFuelRate(fuelUsed * HOUR / duration);
        flight.setReserve(flight.getEndFuel() / flight.getFuelRate());
        flight.setEndTime(new Date((long)duration));
        flight.setDuration(duration);
        flight.setDestination(destination);
        return entityManager.persist(flight);
    }

}
