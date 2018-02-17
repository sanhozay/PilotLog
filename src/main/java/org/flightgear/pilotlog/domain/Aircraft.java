package org.flightgear.pilotlog.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
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

    private double totalFuel, totalDistance;
    private long totalFlights, totalDuration;

    public Aircraft() {}

    public Aircraft(String model) {
        this.model = model;
    }

    public Aircraft(
            String model, Date last,
            float distanceMin, float distanceMax, double distanceAvg,
            float fuelRateMin, float fuelRateMax, double fuelRateAvg,
            int speedMin, int speedMax, double speedAvg,
            double totalDistance, long totalDuration, long totalFlights, double totalFuel
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

    public double getTotalFuel() {
        return totalFuel;
    }

    public void setTotalFuel(double totalFuel) {
        this.totalFuel = totalFuel;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalFlights() {
        return totalFlights;
    }

    public void setTotalFlights(long totalFlights) {
        this.totalFlights = totalFlights;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
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
