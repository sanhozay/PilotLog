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

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Domain object for airport summary.
 *
 * @author Richard Senior
 */
@Entity
@Table(indexes = {
    @Index(columnList = "arrivals"),
    @Index(columnList = "departures"),
})
@SuppressWarnings("WeakerAccess")
public  class Airport implements Serializable  {

    @Id
    private String icao;

    private String name;
    private Coordinate coordinate;

    private int arrivals;
    private int departures;
    private int movements;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date last;

    public Airport() {}

    public Airport(String icao, int arrivals, int departures, Date last) {
        this.icao = icao;
        this.arrivals = arrivals;
        this.departures = departures;
        this.movements = arrivals + departures;
        this.last = last;
    }

    // Equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airport airport = (Airport)o;
        return Objects.equals(icao, airport.icao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(icao);
    }

    // Accessors

    public String getIcao() {
        return icao;
    }

    public void setIcao(String icao) {
        this.icao = icao;
    }

    public int getArrivals() {
        return arrivals;
    }

    public void setArrivals(int arrivals) {
        this.arrivals = arrivals;
        this.movements = arrivals + departures;
    }

    public int getDepartures() {
        return departures;
    }

    public void setDepartures(int departures) {
        this.departures = departures;
        this.movements = arrivals + departures;
    }

    public int getMovements() {
        return movements;
    }

    public void setMovements(int movements) {
        this.movements = movements;
    }

    public Date getLast() {
        return last;
    }

    public void setLast(Date last) {
        this.last = last;
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
