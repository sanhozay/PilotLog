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

package org.flightgear.pilotlog.integration;

import org.flightgear.pilotlog.dto.AirportInfo;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;

@SuppressWarnings("javadoc")
public class AirportInfoRepositoryTest {

    @Test
    public void testAirportInfo() throws Throwable {
        AirportInfoRepository airportInfoRepository = new AirportInfoRepository();
        AirportInfo egnm = airportInfoRepository.getAirportInfo("EGNM");
        assertThat(egnm.getIcao()).isEqualTo("EGNM");
        assertThat(egnm.getName()).isEqualTo("Leeds Bradford");
        assertThat(egnm.getCoordinate().getLatitude()).isCloseTo(53.8f, offset(0.1f));
        assertThat(egnm.getCoordinate().getLongitude()).isCloseTo(-1.6f, offset(0.1f));
    }

}
