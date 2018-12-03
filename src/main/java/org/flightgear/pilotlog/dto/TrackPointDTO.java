package org.flightgear.pilotlog.dto;

import org.flightgear.pilotlog.domain.Coordinate;

/**
 * Interface for DTO for track points.
 *
 * Used to build GeoJSON point structures without the overhead of the TrackPoint entity
 */
public interface TrackPointDTO {

    Coordinate getCoordinate();
    float getAltitude();

}
