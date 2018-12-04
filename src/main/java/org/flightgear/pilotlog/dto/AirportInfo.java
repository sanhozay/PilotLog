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

package org.flightgear.pilotlog.dto;

import org.flightgear.pilotlog.domain.Coordinate;

import java.io.Serializable;

/**
 * Bean to hold data from the airport information data file.
 *
 * @author Richard Senior
 */
public class AirportInfo implements Serializable {

    private String icao;
    private String name;
    private Coordinate coordinate;

    public AirportInfo() {}

    public AirportInfo(String tuple) {
        String[] components = tuple.split(":");
        this.icao = components[0];
        this.name = components[1];
        this.coordinate = new Coordinate(
            Float.parseFloat(components[2]),
            Float.parseFloat(components[3])
        );
    }

    // Accessors

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

}
