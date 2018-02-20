package org.flightgear.pilotlog.domain;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Embedded object to represent a min/max/avg statistic.
 *
 * @param <T> the numeric type of the statistic
 * @author Richard Senior
 */
@Embeddable
public class Statistic<T extends Number> implements Serializable {

    private T min, max;
    private Double avg;

    public Statistic() {}

    public Statistic(T min, T max, Double avg) {
        this.min = min;
        this.max = max;
        this.avg = avg;
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

}
