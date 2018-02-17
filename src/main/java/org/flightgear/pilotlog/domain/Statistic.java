package org.flightgear.pilotlog.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Embedded object to represent a min/max/avg statistic.
 *
 * @param <T> the numeric type of the statistic
 *
 * @author Richard Senior
 */
@Embeddable
public class Statistic<T extends Number> implements Serializable {

    private T min, max;
    private double avg;

    public Statistic() {}

    public Statistic(T min, T max, double avg) {
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public double getAvg() {
        return avg;
    }

}
