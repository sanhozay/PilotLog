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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
 * Domain object for aircraft summary.
 *
 * @author Richard Senior
 */
@Entity
@Table(indexes = {
        @Index(columnList = "totalDistance"),
        @Index(columnList = "totalFuel"),
        @Index(columnList = "totalDuration"),
        @Index(columnList = "totalFlights")
})
@SuppressWarnings("WeakerAccess")
public class Aircraft implements Serializable {

    @Id
    private String model;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date last;

    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "minDistance")),
            @AttributeOverride(name = "max", column = @Column(name = "maxDistance")),
            @AttributeOverride(name = "avg", column = @Column(name = "avgDistance")),
    })
    @Embedded
    private Statistic<Float> distance;

    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "minFuelRate")),
            @AttributeOverride(name = "max", column = @Column(name = "maxFuelRate")),
            @AttributeOverride(name = "avg", column = @Column(name = "avgFuelRate")),
    })
    @Embedded
    private Statistic<Float> fuelRate;

    @AttributeOverrides({
            @AttributeOverride(name = "min", column = @Column(name = "minSpeed")),
            @AttributeOverride(name = "max", column = @Column(name = "maxSpeed")),
            @AttributeOverride(name = "avg", column = @Column(name = "avgSpeed")),
    })
    @Embedded
    private Statistic<Integer> speed;

    private Double totalFuel, totalDistance;
    private Long totalFlights, totalDuration;

    public Aircraft() {}

    public Aircraft(String model) {
        this.model = model;
    }

    public Aircraft(
            String model, Date last,
            Float distanceMin, Float distanceMax, Double distanceAvg,
            Float fuelRateMin, Float fuelRateMax, Double fuelRateAvg,
            Integer speedMin, Integer speedMax, Double speedAvg,
            Double totalDistance, Long totalDuration, Long totalFlights, Double totalFuel
    ) {
        this.model = model;
        this.last = last;
        this.distance = new Statistic<>(distanceMin, distanceMax, distanceAvg);
        this.speed = new Statistic<>(speedMin, speedMax, speedAvg);
        this.fuelRate = new Statistic<>(fuelRateMin, fuelRateMax, fuelRateAvg);
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
        this.totalFlights = totalFlights;
        this.totalFuel = totalFuel;
    }

    // Accessors

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getLast() {
        return last;
    }

    public void setLast(Date last) {
        this.last = last;
    }

    public Statistic<Float> getDistance() {
        return distance;
    }

    public void setDistance(Statistic<Float> distance) {
        this.distance = distance;
    }

    public Statistic<Float> getFuelRate() {
        return fuelRate;
    }

    public void setFuelRate(Statistic<Float> fuelRate) {
        this.fuelRate = fuelRate;
    }

    public Statistic<Integer> getSpeed() {
        return speed;
    }

    public void setSpeed(Statistic<Integer> speed) {
        this.speed = speed;
    }

    public Double getTotalFuel() {
        return totalFuel;
    }

    public void setTotalFuel(Double totalFuel) {
        this.totalFuel = totalFuel;
    }

    public Double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public Long getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(Long totalFlights) {
        this.totalFlights = totalFlights;
    }

    public Long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Long totalDuration) {
        this.totalDuration = totalDuration;
    }

    // Equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aircraft that = (Aircraft)o;
        return Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model);
    }

}
