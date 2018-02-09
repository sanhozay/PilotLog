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

import org.springframework.stereotype.Component;

/**
 * View helper to format flight durations.
 *
 * @author Richard Senior
 */
@Component
public class DurationFormatter {

    /**
     * Formats a duration, given in minutes, into HH:MM format.
     *
     * @param minutes the duration in minutes
     * @return the duration formatted in the format HH:MM
     */
    public String format(Integer minutes) {
        return minutes != null ? String.format("%d:%02d", minutes / 60, minutes % 60) : null;
    }

}
