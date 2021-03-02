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

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import org.flightgear.pilotlog.domain.Coordinate;

/**
 * Interface for DTO for track points.
 *
 * Used when querying tracks for GeoJSON track and altitude profile.
 */
public interface TrackPointDTO {

    Coordinate getCoordinate();
    float getAltitude();

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Date getTimestamp();

}
