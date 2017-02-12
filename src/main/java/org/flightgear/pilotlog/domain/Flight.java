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

package org.flightgear.pilotlog.domain;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Domain object representing a flight.
 *
 * @author Richard Senior
 */
@Entity
@SuppressWarnings("serial")
@JacksonXmlRootElement(localName = "PropertyList")
@JsonPropertyOrder({"id", "callsign", "aircraft", "origin", "startTime", "startFuel", "startOdometer",
    "destination", "endTime", "endFuel", "endOdometer", "fuelUsed", "fuelRate", "duration",
    "distance", "status"})
@Table(indexes = {
    @Index(columnList = "aircraft", unique = false),
    @Index(columnList = "callsign", unique = false),
    @Index(columnList = "origin", unique = false),
    @Index(columnList = "destination", unique = false),
})
public class Flight implements Serializable, Comparable<Flight> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String callsign, aircraft, origin, destination;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date endTime;

    private Double startFuel, endFuel, startOdometer, endOdometer;

    @Enumerated(EnumType.STRING)
    private FlightStatus status;

    // Computed fields

    private Long duration;
    private Double fuelUsed, fuelRate, distance;

    public Flight() {}

    public Flight(String callsign, String aircraft, String origin, double startFuel, double startOdometer) {
        this.callsign = callsign;
        this.aircraft = aircraft;
        this.origin = origin;
        this.startFuel = startFuel;
        this.startOdometer = startOdometer;
    }

    /**
     * Updates computed fields of flight; duration, fuelUsed and fuelRate.
     */
    public void updateComputedFields() {
        if (startTime != null && endTime != null) {
            duration = Duration.between(startTime.toInstant(), endTime.toInstant()).toMinutes();
            if (startFuel != null && endFuel != null) {
                fuelUsed = startFuel - endFuel;
                fuelRate = fuelUsed == 0.0 ? null : 60 * fuelUsed / duration;
            }
            if (startOdometer != null && endOdometer != null) {
                distance = endOdometer - startOdometer;
            }
        }
    }

    // Accessors

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public Double getStartFuel() {
        return startFuel;
    }

    public void setStartFuel(Double startFuel) {
        this.startFuel = startFuel;
    }

    public Double getEndFuel() {
        return endFuel;
    }

    public void setEndFuel(Double endFuel) {
        this.endFuel = endFuel;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Double getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(Double fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public Double getFuelRate() {
        return fuelRate;
    }

    public void setFuelRate(Double fuelRate) {
        this.fuelRate = fuelRate;
    }

    public Double getStartOdometer() {
        return startOdometer;
    }

    public void setStartOdometer(Double startOdometer) {
        this.startOdometer = startOdometer;
    }

    public Double getEndOdometer() {
        return endOdometer;
    }

    public void setEndOdometer(Double endOdometer) {
        this.endOdometer = endOdometer;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    // Comparison and equality

    @Override
    public int compareTo(Flight o) {
        return getStartTime().compareTo(o.getStartTime());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((startTime == null) ? 0 : startTime.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Flight other = (Flight)obj;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("with id %d in %s from %s", id, aircraft, origin));
        if (destination != null) {
            sb.append(String.format(" to %s", destination));
        }
        sb.append(String.format(", status %s", status));
        return sb.toString();
    }

}
