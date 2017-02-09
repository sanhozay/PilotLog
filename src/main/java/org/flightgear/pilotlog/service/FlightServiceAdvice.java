/*
 * PilotLog
 *
 * Copyright (c) 2017 Richard Senior
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

import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.flightgear.pilotlog.domain.Flight;
import org.flightgear.pilotlog.domain.FlightRepository;
import org.flightgear.pilotlog.domain.FlightStatus;
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

    Logger log = LoggerFactory.getLogger(FlightServiceAdvice.class);

    @Autowired(required = true)
    FlightRepository dao;

    @Before("execution (* FlightService.beginFlight(..))")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void purge() {
        final Set<Flight> toDelete = new HashSet<>();
        if (toDelete.addAll(dao.findByStatus(FlightStatus.INVALID)) ||
            toDelete.addAll(dao.findByStatus(FlightStatus.ACTIVE))) {
            for (final Flight flight : toDelete) {
                dao.delete(flight);
                log.info("Deleted flight {}", flight);
            }
        }
    }

    @AfterReturning("execution (* FlightService.endFlight(..))")
    @Transactional(propagation = Propagation.MANDATORY)
    public void compute(JoinPoint jp) {
        final long id = (long)jp.getArgs()[0];
        final Flight flight = dao.findOne(id);
        flight.updateComputedFields();
        if (flight.getDuration() < 1) {
            flight.setStatus(FlightStatus.INVALID);
        }
        log.info("Updated computed fields of flight {}", flight);
    }

}
