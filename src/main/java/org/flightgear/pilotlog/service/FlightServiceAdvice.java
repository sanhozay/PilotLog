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
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

/**
 * AOP advice for flight service operations.
 *
 * @author Richard Senior
 */
@Aspect
@Component
public class FlightServiceAdvice {

    private static final Logger log = LoggerFactory.getLogger(FlightServiceAdvice.class);

    private FlightRepository flightRepository;
    private AircraftService aircraftService;

    @Before("execution (* FlightService.beginFlight(..))")
    @Transactional(propagation = Propagation.REQUIRED)
    public void purge() {
        final Set<Flight> toDelete = new HashSet<>();
        if (toDelete.addAll(flightRepository.findByStatus(FlightStatus.INVALID)) ||
                toDelete.addAll(flightRepository.findByStatus(FlightStatus.ACTIVE)))
            for (final Flight flight : toDelete) {
                flightRepository.delete(flight);
                log.info("Deleted flight {}", flight);
            }
    }

    @AfterReturning("endFlight() || updateFlight()")
    @Transactional(propagation = Propagation.MANDATORY)
    public void compute(final JoinPoint jp) {
        final int id = (int)jp.getArgs()[0];
        final Flight flight = flightRepository.findOne(id);
        flight.updateComputedFields();
        if (flight.getDuration() == 0 && flight.getStatus() == FlightStatus.COMPLETE) {
            flight.setStatus(FlightStatus.INVALID);
            log.warn("Invalidating flight {} because duration is zero", flight.getId());
        }
        log.info("Updated computed fields of flight {}", flight);
    }

    @AfterReturning("endFlight()")
    @Transactional(propagation = Propagation.MANDATORY)
    public void summarize(final JoinPoint jp) {
        final int id = (int)jp.getArgs()[0];
        final Flight flight = flightRepository.findOne(id);
        aircraftService.updateSummary(flight.getAircraft());
        log.info("Updated summary with flight {}", flight);
    }

    @Pointcut("execution (* FlightService.endFlight(..))")
    private void endFlight() {}

    @Pointcut("execution (* FlightService.updateFlight(..))")
    private void updateFlight() {}

    @Autowired
    public void setFlightRepository(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Autowired
    public void setAircraftService(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

}
