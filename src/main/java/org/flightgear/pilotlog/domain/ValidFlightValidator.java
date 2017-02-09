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

package org.flightgear.pilotlog.domain;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Custom validator for flights.
 *
 * @author Richard Senior
 */
public class ValidFlightValidator implements ConstraintValidator<ValidFlight, Flight> {

    @Override
    public void initialize(ValidFlight constraintAnnotation) {}

    @Override
    public boolean isValid(Flight flight, ConstraintValidatorContext context) {
        if (flight.getStatus().equals(FlightStatus.COMPLETE)) {

            context.disableDefaultConstraintViolation();

            if (flight.getDestination() == null) {
                context.buildConstraintViolationWithTemplate("Destination is required")
                    .addPropertyNode("destination")
                    .addConstraintViolation();
                return false;
            }

            if (flight.getEndFuel() == null) {
                context.buildConstraintViolationWithTemplate("End fuel is required")
                    .addPropertyNode("endFuel")
                    .addConstraintViolation();
                return false;
            }

            if (flight.getEndTime() == null) {
                context.buildConstraintViolationWithTemplate("End time is required")
                    .addPropertyNode("endTime")
                    .addConstraintViolation();
                return false;
            }

            if (flight.getEndOdometer() == null) {
                context.buildConstraintViolationWithTemplate("End odometer is required")
                    .addPropertyNode("endOdometer")
                    .addConstraintViolation();
                return false;
            }

            if (flight.getEndTime().compareTo(flight.getStartTime()) < 0) {
                context.buildConstraintViolationWithTemplate("End time is before start time")
                    .addPropertyNode("endTime")
                    .addConstraintViolation();
                return false;
            }

            if (flight.getEndOdometer() <= flight.getStartOdometer()) {
                context.buildConstraintViolationWithTemplate("End odometer is less than start odometer")
                    .addPropertyNode("endOdometer")
                    .addConstraintViolation();
                return false;
            }
        }
        return true;
    }

}
