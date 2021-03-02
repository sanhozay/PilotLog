/*
 * PilotLog
 *
 * Copyright © 2019 Richard Senior
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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.flightgear.pilotlog.domain.Coordinate;
import org.flightgear.pilotlog.domain.FlightStatus;

import java.util.Date;

@SuppressWarnings("unused")
public class FlightDTO {

    private int id;
    private boolean complete;
    private Coordinate coordinate;
    private Date startTime, endTime, timestamp;
    private FlightStatus status;
    private Float currentAltitude, heading;
    private Float startOdometer, endOdometer, distance;
    private Float startFuel, endFuel, fuelUsed, fuelRate, reserve;
    private Integer altitude, duration, groundSpeed;
    private String callsign;
    private String aircraft;
    private String origin;

    public Float getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(Float startOdometer) {
        this.startOdometer = startOdometer;
    }

    public Float getEndOdometer() {
        return endOdometer;
    }

    public void setEndOdometer(Float endOdometer) {
        this.endOdometer = endOdometer;
    }

    public Float getStartFuel() {
        return startFuel;
    }

    public void setStartFuel(Float startFuel) {
        this.startFuel = startFuel;
    }

    public Float getEndFuel() {
        return endFuel;
    }

    public void setEndFuel(Float endFuel) {
        this.endFuel = endFuel;
    }

    public Float getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(Float fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public Float getFuelRate() {
        return fuelRate;
    }

    public void setFuelRate(Float fuelRate) {
        this.fuelRate = fuelRate;
    }

    private String destination;

    // Accessors

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    @JsonProperty("cruiseAltitude")
    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getReserve() {
        return reserve;
    }

    public void setReserve(Float reserve) {
        this.reserve = reserve;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(Integer groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public String getAircraft() {
        return aircraft;
    }

    public void setAircraft(String aircraft) {
        this.aircraft = aircraft;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("altitude")
    public Float getCurrentAltitude() {
        return currentAltitude;
    }

    public void setCurrentAltitude(Float currentAltitude) {
        this.currentAltitude = currentAltitude;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

}
