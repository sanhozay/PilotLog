package org.flightgear.pilotlog.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("javadoc")
public class InvalidFlightStatusExceptionTest {

    @Test
    public void testInvalidFlightStatusException() {
        InvalidFlightStatusException e = new InvalidFlightStatusException("message");
        assertThat(e.getMessage()).isEqualTo("message");
    }

    @Test
    public void testInvalidFlightStatusThrowableException() {
        InvalidFlightStatusException e = new InvalidFlightStatusException("message", new IllegalArgumentException());
        assertThat(e.getMessage()).isEqualTo("message");
        assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
    }

}
