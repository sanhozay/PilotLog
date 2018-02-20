package org.flightgear.pilotlog.domain;

import net.sf.beanrunner.BeanRunner;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("javadoc")
public class FlightTest {

    @Test
    public void testBean() throws Exception {
        BeanRunner beanRunner = new BeanRunner();
        beanRunner.addTestValue(FlightStatus.class, FlightStatus.COMPLETE);
        beanRunner.testBean(new Flight());
    }

    @Test
    public void testIsCompleteNew() {
        // Given a new flight
        Flight flight = new Flight();
        // expect it not to be complete
        assertThat(flight.isComplete()).isFalse();
    }

    @Test
    public void testIsCompleteActive() {
        // Given an active flight
        Flight flight = new Flight();
        flight.setStatus(FlightStatus.ACTIVE);
        // expect it not to be complete
        assertThat(flight.isComplete()).isFalse();
    }

    @Test
    public void testIsCompleteInvalid() {
        // Given an invalid flight
        Flight flight = new Flight();
        flight.setStatus(FlightStatus.INVALID);
        // expect it not to be complete
        assertThat(flight.isComplete()).isFalse();
    }

    @Test
    public void testIsCompleteComplete() {
        // Given a completed flight
        Flight flight = new Flight();
        flight.setStatus(FlightStatus.COMPLETE);
        // expect it to be complete
        assertThat(flight.isComplete()).isTrue();
    }

    @Test
    public void testToStringActive() {
        // Given an active flight with departure properties set
        Flight flight = new Flight("G-SHOZ", "707", "EGNM", 100.0f, 0.0f);
        flight.setStatus(FlightStatus.ACTIVE);
        flight.setId(100);
        // expect a suitable toString result
        assertThat(flight.toString()).isEqualTo("#100 in 707 from EGNM, status ACTIVE");
    }

    @Test
    public void testToStringComplete() {
        // Given a completed flight with departure and arrival properties set
        Flight flight = new Flight("G-SHOZ", "707", "EGNM", 100.0f, 0.0f);
        flight.setStatus(FlightStatus.COMPLETE);
        flight.setDestination("EGLL");
        flight.setId(100);
        // expect a suitable toString result
        assertThat(flight.toString()).isEqualTo("#100 in 707 from EGNM to EGLL, status COMPLETE");
    }

    @Test
    @SuppressWarnings("all")
    public void testEqualsSame() {
        // Given two identical flights
        Flight f1 = new Flight();
        Flight f2 = new Flight();
        // expect the usual equatable rules to apply
        assertThat(f1.equals(null)).isFalse();
        assertThat(f1.equals("")).isFalse();
        assertThat(f1.equals(f1)).isTrue();
        assertThat(f1).isEqualTo(f2);
    }

    @Test
    public void testEqualsDifferent() {
        // Given two different flights
        Flight f1 = new Flight();
        f1.setStartTime(new Date(0L));
        Flight f2 = new Flight();
        f2.setStartTime(new Date(1L));
        // expect them not to be equal
        assertThat(f1).isNotEqualTo(f2);
    }

    @Test
    public void testHashCodeSame() {
        // Given two identical flights
        Flight f1 = new Flight();
        Flight f2 = new Flight();
        // expect their hash codes to be equal
        assertThat(f1.hashCode()).isEqualTo(f2.hashCode());
    }

    @Test
    public void testHashCode() {
        // Given two different flights
        Flight f1 = new Flight();
        f1.setStartTime(new Date(0L));
        Flight f2 = new Flight();
        f2.setStartTime(new Date(1L));
        // expect their hash codes to be different
        assertThat(f1.hashCode()).isNotEqualTo(f2.hashCode());
    }

}
