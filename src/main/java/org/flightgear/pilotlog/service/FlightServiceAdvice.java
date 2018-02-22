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

package org.flightgear.pilotlog.service;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.flightgear.pilotlog.domain.Flight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * AOP advice for flight service operations.
 *
 * @author Richard Senior
 */
@Aspect
@Component
public class FlightServiceAdvice {

    private static final Logger log = LoggerFactory.getLogger(FlightServiceAdvice.class);

    private AircraftService aircraftService;
    private FlightService flightService;

    @Before("execution (* FlightService.beginFlight(..))")
    @Transactional(propagation = Propagation.REQUIRED)
    public void purge() {
        flightService.purge();
        log.info("Purged invalid and active flights");
    }

    @AfterReturning("endFlight() || updateFlight()")
    @Transactional(propagation = Propagation.MANDATORY)
    public void compute(final JoinPoint jp) {
        final int id = (int)jp.getArgs()[0];
        final Flight flight = flightService.findFlightById(id);
        flightService.updateComputedFields(flight);
        log.info("Updated computed fields of flight {}", flight);
    }

    @AfterReturning("endFlight()")
    @Transactional(propagation = Propagation.MANDATORY)
    public void summarize(final JoinPoint jp) {
        final int id = (int)jp.getArgs()[0];
        final Flight flight = flightService.findFlightById(id);
        aircraftService.updateSummary(flight.getAircraft());
        log.info("Updated summary with flight {}", flight);
    }

    @Pointcut("execution (* FlightService.endFlight(..))")
    void endFlight() {}

    @Pointcut("execution (* FlightService.updateFlight(..))")
    void updateFlight() {}

    @Autowired
    public void setAircraftService(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @Autowired
    public void setFlightService(FlightService FlightService) {
        this.flightService = FlightService;
    }

}
