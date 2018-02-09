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

package org.flightgear.pilotlog.web.helpers;

/**
 * Value object for flight totals.
 *
 * @author Richard Senior
 */
public class FlightRecordTotals {

    private int pageTotal, grandTotal;

    public FlightRecordTotals(int pageTotal, int grandTotal) {
        this.pageTotal = pageTotal;
        this.grandTotal = grandTotal;
    }

    // Other Methods

    /**
     * Gets the difference between the grand total and page total.
     *
     * @return the "other" total, i.e. outwith the page total
     */
    public int getOtherTotal() {
        return grandTotal - pageTotal;
    }

    // Accessors

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(int grandTotal) {
        this.grandTotal = grandTotal;
    }

}
