package org.flightgear.pilotlog.service;

import org.aspectj.lang.JoinPoint;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"javadoc", "unchecked"})
public class FlightServiceAdviceTest {

    @Mock
    private AircraftService aircraftService;

    @Mock
    private FlightService flightService;

    @Mock
    private JoinPoint joinPoint;

    private FlightServiceAdvice flightServiceAdvice;

    @Before
    public void setUp() {
        flightServiceAdvice = new FlightServiceAdvice();
        flightServiceAdvice.setAircraftService(aircraftService);
        flightServiceAdvice.setFlightService(flightService);
        flightServiceAdvice.updateFlight();
        flightServiceAdvice.endFlight();

        when(flightService.findFlightById(anyInt())).thenAnswer((Answer<Flight>)invocation -> {
            Flight flight = new Flight("G-SHOZ", "707", "EGNM", 1000.0f, 0.0f);
            flight.setStatus(FlightStatus.COMPLETE);
            flight.setStartTime(new Date(0L));
            flight.setEndTime(new Date(1000 * 3600L));
            flight.setDuration(3600);
            return flight;
        });
        when(joinPoint.getArgs()).thenReturn(new Integer[]{0});
    }

    @Test
    public void testPurge() {
        // When purging flights
        flightServiceAdvice.purge();
        // expect the flight service to purge flights
        verify(flightService).purge();
    }

    @Test
    public void testCompute() {
        // When computing fields on flights
        flightServiceAdvice.compute(joinPoint);
        // expect the flight service to look for the flight inidcated in the joinpoint
        verify(flightService).findFlightById(0);
        // and the flight service to update computed fields
        verify(flightService).updateComputedFields(any(Flight.class));
    }

    @Test
    public void testSummarize() {
        // When summarizing flights
        flightServiceAdvice.summarize(joinPoint);
        // expect the flight service to look for the flight inidcated in the joinpoint
        verify(flightService).findFlightById(0);
        // and the flight service to update the summary for an aircraft
        verify(aircraftService).updateSummary(any(String.class));
    }

}
