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

package org.flightgear.pilotlog.web.helpers

import spock.lang.Specification

/**
 * Tests for duration formatter
 *
 * @author Richard Senior
 */
class TestDurationFormatter extends Specification {

    def"Check formatting of simple durations"() {

        given:
        def df = new DurationFormatter()

        expect:
        df.format(mins) == formatted

        where:
        mins|| formatted
        0   || "0:00"
        9   || "0:09"
        10  || "0:10"
        59  || "0:59"
        60  || "1:00"
        61  || "1:01"
        599 || "9:59"
        600 || "10:00"
        601 || "10:01"
    }
}
