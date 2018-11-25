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

/**
 * Class to represent a paged total.
 *
 * @param <T> the numeric type of the total
 * @author Richard Senior
 */
@SuppressWarnings({"unchecked", "WeakerAccess"})
public class Total<T extends Number> {

    private final T total;
    private final T pageTotal;

    /**
     * Construct a total.
     *
     * @param pageTotal the total for the current page
     * @param total the grand total
     */
    public Total(T pageTotal, T total) {
        this.pageTotal = pageTotal;
        this.total = total;
    }

    /**
     * Gets the total for this page.
     *
     * @return the page total
     */
    public T getPageTotal() {
        return pageTotal;
    }

    /**
     * Gets the combined total for the other pages.
     *
     * @return the other total
     */
    public T getOtherTotal() {
        if (total instanceof Float) {
            return (T)Float.valueOf(total.floatValue() - pageTotal.floatValue());
        }
        if (total instanceof Double) {
            return (T)Double.valueOf(total.doubleValue() - pageTotal.doubleValue());
        }
        if (total instanceof Integer) {
            return (T)Integer.valueOf(total.intValue() - pageTotal.intValue());
        }
        if (total instanceof Long) {
            return (T)Long.valueOf(total.longValue() - pageTotal.longValue());
        }
        throw new IllegalArgumentException("Unsupported numeric type");
    }

    /**
     * Gets the overall total.
     *
     * @return the total
     */
    public T getTotal() {
        return total;
    }

}
