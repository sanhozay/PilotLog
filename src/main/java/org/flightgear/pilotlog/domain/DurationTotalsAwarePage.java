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

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Pageable implementation that has awareness of duration totals.
 *
 * This is intended to be serialized to JSON or XML by an object mapper.
 *
 * @param <T> a type that implements the Timed interface
 *
 * @author Richard Senior
 */
public class DurationTotalsAwarePage<T extends Timed> extends PageImpl<T> {

    private DurationTotals<T> durationTotals;

    /**
     * Construct a duration totals aware page.
     *
     * @param content the page content, a list of objects that implement Timed
     * @param pageable the pageable to apply to the page
     * @param total the total number of elements in the result set
     * @param totalDuration the total duration of the elements
     */
    public DurationTotalsAwarePage(List<T> content, Pageable pageable, long total, int totalDuration) {
        super(content, pageable, total);
        durationTotals = new DurationTotals<>(content, totalDuration);
    }

    /**
     * Get a durations totals value object.
     *
     * @return the durations totals
     */
    public DurationTotals getDurationTotals() {
        return durationTotals;
    }
}
