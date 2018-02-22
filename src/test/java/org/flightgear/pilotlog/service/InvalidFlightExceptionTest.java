package org.flightgear.pilotlog.service;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("javadoc")
public class InvalidFlightExceptionTest {

    @Test
    public void testInvalidFlightException() {
        InvalidFlightException e = new InvalidFlightException("message");
        assertThat(e.getMessage()).isEqualTo("message");
    }

}
