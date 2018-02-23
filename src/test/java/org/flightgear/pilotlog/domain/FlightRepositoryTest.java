package org.flightgear.pilotlog.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flightgear.pilotlog.domain.FlightStatus.ACTIVE;
import static org.flightgear.pilotlog.domain.FlightStatus.COMPLETE;
import static org.flightgear.pilotlog.domain.FlightStatus.INVALID;
import static org.flightgear.pilotlog.domain.FlightStatus.NEW;

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
        Flight flight = new FlightBuilder("707", "EGNM").status(ACTIVE).build();
        // expect the aircraft summary for the aircraft to be null
        assertThat(flightRepository.aircraftSummaryByModel(flight.getAircraft())).isNull();
    }

    @Test
    public void testSummaryWithOneInvalidFlight() {
        // Given a database with a single invalid flight
        Flight flight = new FlightBuilder("707", "EGNM").status(INVALID).build();
        // expect the aircraft summary for the aircraft to be null
        assertThat(flightRepository.aircraftSummaryByModel(flight.getAircraft())).isNull();
    }

    @Test
    public void testSummaryWithOneCompleteFlight() {
        // Given a database with a single completed flight
        Flight flight = new FlightBuilder("707", "EGNM").complete("EGLL", HOUR, 100.0f, 200.0f).build();
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

    private class FlightBuilder {

        private String aircraft, origin, destination;
        private FlightStatus status;
        private Integer duration;
        private Float fuelUsed, distance;

        FlightBuilder(String aircraft, String origin) {
            this.aircraft = aircraft;
            this.origin = origin;
            this.status = NEW;
        }

        FlightBuilder complete(String destination, int duration, float fuel, float distance) {
            return status(COMPLETE).destination(destination).duration(duration).fuel(fuel).distance(distance);
        }

        FlightBuilder destination(String destination) {
            this.destination = destination;
            return this;
        }

        FlightBuilder distance(float distance) {
            this.distance = distance;
            return this;
        }

        FlightBuilder duration(int duration) {
            this.duration = duration;
            return this;
        }

        FlightBuilder fuel(float fuelUsed) {
            this.fuelUsed = fuelUsed;
            return this;
        }

        FlightBuilder status(FlightStatus status) {
            this.status = status;
            return this;
        }

        Flight build() {
            Flight flight = new Flight("G-SHOZ", aircraft, origin, 1000.0f, 0.0f);
            flight.setStartTime(new Date(0L));
            flight.setStatus(status);
            flight.setAltitude(10000);
            if (distance != null) {
                flight.setEndOdometer(flight.getStartOdometer() + distance);
                flight.setDistance(distance);
            }
            if (duration != null) {
                flight.setEndTime(new Date((long)duration));
                flight.setDuration(duration);
            }
            if (fuelUsed != null) {
                flight.setEndFuel(flight.getStartFuel() - fuelUsed);
                flight.setFuelUsed(fuelUsed);
            }
            if (distance != null && duration != null) {
                flight.setGroundSpeed((int)(HOUR * distance / duration));
            }
            if (fuelUsed != null && duration != null) {
                flight.setFuelRate(fuelUsed * HOUR / duration);
                flight.setReserve(flight.getEndFuel() / flight.getFuelRate());
            }
            flight.setDestination(destination);
            return entityManager.persist(flight);
        }

    }

}

