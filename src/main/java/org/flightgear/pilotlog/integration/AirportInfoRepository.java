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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository for airport information.
 *
 * @author Richard Senior
 */
@Component
public class AirportInfoRepository {

    private static final String AIRPORT_DATABASE = "airports.dat";

    private final Map<String, AirportInfo> airportInfo = new HashMap<>();

    public AirportInfoRepository() throws IOException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource(AIRPORT_DATABASE);
        if (url != null) {
            Files.readAllLines(Paths.get(url.toURI())).parallelStream().forEach(tuple -> {
                AirportInfo info = new AirportInfo(tuple);
                airportInfo.put(info.getIcao(), info);
            });
        }
    }

    public AirportInfo getAirportInfo(String icao) {
        return airportInfo.get(icao);
    }

}
