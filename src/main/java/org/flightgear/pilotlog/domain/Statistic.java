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

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Embedded object to represent a min/max/avg statistic.
 *
 * @param <T> the numeric type of the statistic
 * @author Richard Senior
 */
@Embeddable
@SuppressWarnings("WeakerAccess")
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
