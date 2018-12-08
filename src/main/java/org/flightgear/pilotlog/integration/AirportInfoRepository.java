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
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for airport information.
 *
 * @author Richard Senior
 */
@Component
public class AirportInfoRepository {

    private static final String AIRPORT_DATABASE = "/airports.dat";

    private final Map<String, AirportInfo> airportInfo = new HashMap<>();

    public AirportInfoRepository() throws IOException {
        ClassPathResource data = new ClassPathResource(AIRPORT_DATABASE);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(data.getInputStream()))) {
            String line = in.readLine();
            while (line != null) {
                AirportInfo info = new AirportInfo(line);
                airportInfo.put(info.getIcao(), info);
                line = in.readLine();
            }
        }
    }

    public AirportInfo getAirportInfo(String icao) {
        return airportInfo.get(icao);
    }

}
