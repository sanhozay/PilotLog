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

import org.flightgear.pilotlog.domain.Flight;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Custom validator for flights.
 *
 * @author Richard Senior
 */
@Component
public class FlightValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Flight.class);
    }

    private void validateActive(Flight flight, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "callsign", "callsign.empty", "Callsign is required");
        ValidationUtils.rejectIfEmpty(errors, "aircraft", "aircraft.empty", "Aircraft is required");
        ValidationUtils.rejectIfEmpty(errors, "origin", "origin.empty", "Origin is required");
        ValidationUtils.rejectIfEmpty(errors, "startFuel", "startFuel.empty", "Start fuel is required");
        ValidationUtils.rejectIfEmpty(errors, "startOdometer", "startOdometer.empty", "Start odometer is required");
        ValidationUtils.rejectIfEmpty(errors, "startTime", "startTime.empty", "Start time is required");
    }

    private void validateComplete(Flight flight, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "callsign", "callsign.empty", "Callsign is required");
        ValidationUtils.rejectIfEmpty(errors, "aircraft", "aircraft.empty", "Aircraft is required");
        ValidationUtils.rejectIfEmpty(errors, "origin", "origin.empty", "Origin is required");
        ValidationUtils.rejectIfEmpty(errors, "startFuel", "startFuel.empty", "Start fuel is required");
        ValidationUtils.rejectIfEmpty(errors, "startOdometer", "startOdometer.empty", "Start odometer is required");
        ValidationUtils.rejectIfEmpty(errors, "startTime", "startTime.empty", "Start time is required");
        ValidationUtils.rejectIfEmpty(errors, "destination", "destination.empty", "Destination is required");
        ValidationUtils.rejectIfEmpty(errors, "endFuel", "endFuel.empty", "End fuel is required");
        ValidationUtils.rejectIfEmpty(errors, "endOdometer", "endOdometer.empty", "End odometer is required");
        ValidationUtils.rejectIfEmpty(errors, "endTime", "endTime.empty", "End time is required");
    }

    @Override
    public void validate(Object target, Errors errors) {
        final Flight flight = (Flight)target;
        if (flight.getStatus() == null) {
            ValidationUtils.rejectIfEmpty(errors, "status", "flight.status.empty");
            return;
        }
        switch (flight.getStatus()) {
        case ACTIVE:
            validateActive(flight, errors);
            break;
        case COMPLETE:
            validateComplete(flight, errors);
            break;
        default:
            return;
        }
    }

}
