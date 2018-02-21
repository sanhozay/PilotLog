package org.flightgear.pilotlog.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("javadoc")
public class FlightNotFoundExceptionTest {

    @Test
    public void testFlightNotFoundException() {
        FlightNotFoundException e = new FlightNotFoundException("message");
        assertThat(e.getMessage()).isEqualTo("message");
    }

    @Test
    public void testFlightNotFoundThrowableException() {
        FlightNotFoundException e = new FlightNotFoundException("message", new IllegalArgumentException());
        assertThat(e.getMessage()).isEqualTo("message");
        assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

}
