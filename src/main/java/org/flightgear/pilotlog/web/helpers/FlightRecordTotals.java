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

package org.flightgear.pilotlog.web.helpers;

/**
 * Value object for flight totals.
 *
 * @author Richard Senior
 */
public class FlightRecordTotals {

    private long pageTotal, grandTotal;

    public FlightRecordTotals(long pageTotal, long grandTotal) {
        this.pageTotal = pageTotal;
        this.grandTotal = grandTotal;
    }

    // Other Methods

    /**
     * Gets the difference between the grand total and page total.
     *
     * @return the "other" total, i.e. outwith the page total
     */
    public long getOtherTotal() {
        return grandTotal - pageTotal;
    }

    // Accessors

    public long getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(long pageTotal) {
        this.pageTotal = pageTotal;
    }

    public long getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(long grandTotal) {
        this.grandTotal = grandTotal;
    }

}
