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
import java.util.Map;

/**
 * Extension of PageImpl to incorrporate totals.
 *
 * @param <T> the type
 */
public class TotalsAwarePage<T> extends PageImpl<T> {

    private final Map<String, Total> totals;

    public TotalsAwarePage(
            List<T> content,
            Pageable pageable,
            long totalElements,
            Map<String, Total> totals
    ) {
        super(content, pageable, totalElements);
        this.totals = totals;
    }

    public Map<String, Total> getTotals() {
        return totals;
    }

}
