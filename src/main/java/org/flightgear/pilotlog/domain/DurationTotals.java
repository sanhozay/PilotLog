package org.flightgear.pilotlog.domain;

import java.util.List;

/**
 * Value object for duration totals on paged result sets.
 *
 * @param <T> a type that implements the Timed interface
 *
 * @author Richard Senior
 */
@SuppressWarnings("WeakerAccess")
public class DurationTotals<T extends Flight> {

    private final int totalDuration;
    private List<T> content;

    public DurationTotals(List<T> content, int totalDuration) {
        this.content = content;
        this.totalDuration = totalDuration;
    }

    /**
     * Get the total duration for the current page.
     *
     * @return the page duration
     */
    public int getPageDuration() {
        return content
                .parallelStream()
                .filter(flight -> flight.getStatus() == FlightStatus.COMPLETE)
                .mapToInt(Flight::getDuration)
                .sum();
    }

    /**
     * Get the total duration for pages other than the current page.
     *
     * @return the other duration
     */
    public int getOtherDuration() {
        return totalDuration - getPageDuration();
    }

    /**
     * Get the total duration across all pages.
     *
     * @return the total duration
     */
    public int getTotalDuration() {
        return totalDuration;
    }

}
