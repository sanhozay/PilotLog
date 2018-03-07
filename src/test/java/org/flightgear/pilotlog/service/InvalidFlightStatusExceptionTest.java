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
