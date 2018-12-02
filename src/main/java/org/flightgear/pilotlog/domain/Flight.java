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

package org.flightgear.pilotlog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Domain object representing a flight.
 *
 * @author Richard Senior
 */
@Entity
@SuppressWarnings("serial")
@JacksonXmlRootElement(localName = "PropertyList")
@JsonPropertyOrder({"id", "callsign", "aircraft", "origin", "startTime", "startFuel", "startOdometer", "heading",
        "destination", "endTime", "endFuel", "endOdometer", "fuelUsed", "fuelRate",
        "distance", "groundSpeed", "duration", "status"})
@Table(indexes = {
        @Index(columnList = "aircraft"),
        @Index(columnList = "callsign"),
        @Index(columnList = "origin"),
        @Index(columnList = "destination"),
})
public class Flight implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flight")
    @SequenceGenerator(name = "flight", sequenceName = "flight_sequence", allocationSize = 1)
    private int id;

    private String callsign, aircraft, origin, destination;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date endTime;

    private Float startFuel, endFuel, startOdometer, endOdometer;

    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL)
    @OrderBy("odometer")
    @JsonIgnore
    private List<TrackPoint> track;

    // Computed fields

    private Integer duration, groundSpeed, altitude;
    private Float fuelUsed, fuelRate, reserve, distance, heading;
    private Boolean tracked;

    public Flight() {}

    public Flight(String callsign, String aircraft, String origin, float startFuel, float startOdometer) {
        this.callsign = callsign;
        this.aircraft = aircraft;
        this.origin = origin;
        this.startFuel = startFuel;
        this.startOdometer = startOdometer;
    }

    /**
     * Convenience function to check if a flight is complete.
     *
     * @return true if the status if COMPLETE
     */
    @Transient
    public boolean isComplete() {
        return status == FlightStatus.COMPLETE;
    }

    /**
     * Convenience method to add a track point.
     *
     * @param trackPoint the track point to add
     */
    public void addTrackPoint(TrackPoint trackPoint) {
        if (track == null) {
            track = new ArrayList<>();
        }
        trackPoint.setFlight(this);
        track.add(trackPoint);
        tracked = true;
    }

    // Accessors

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Integer getGroundSpeed() {
        return groundSpeed;
    }

    public void setGroundSpeed(Integer groundSpeed) {
        this.groundSpeed = groundSpeed;
    }

    public Integer getAltitude() {
        return altitude;
    }

    public void setAltitude(Integer altitude) {
        this.altitude = altitude;
    }

    public Float getReserve() {
        return reserve;
    }

    public void setReserve(Float reserve) {
        this.reserve = reserve;
    }

    public List<TrackPoint> getTrack() {
        return track;
    }

    public void setTrack(List<TrackPoint> track) {
        this.track = track;
    }

    public Float getHeading() {
        return heading;
    }

    public void setHeading(Float heading) {
        this.heading = heading;
    }

    public Boolean isTracked() {
        return tracked;
    }

    public void setTracked(Boolean tracked) {
        this.tracked = tracked;
    }

    // Comparison and equality

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight)o;
        return Objects.equals(callsign, flight.callsign) &&
            Objects.equals(aircraft, flight.aircraft) &&
            Objects.equals(origin, flight.origin) &&
            Objects.equals(startTime, flight.startTime) &&
            Objects.equals(startFuel, flight.startFuel) &&
            Objects.equals(startOdometer, flight.startOdometer) &&
            status == flight.status &&
            Objects.equals(altitude, flight.altitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(callsign, aircraft, origin, startTime, startFuel, startOdometer, status, altitude);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("#%d in %s from %s", id, aircraft, origin));
        if (destination != null) {
            sb.append(String.format(" to %s", destination));
        }
        sb.append(String.format(", status %s", status));
        return sb.toString();
    }

}
