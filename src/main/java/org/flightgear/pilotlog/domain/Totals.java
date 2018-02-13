package org.flightgear.pilotlog.domain;

import java.util.List;

public class Totals<T extends Timed> {

    private final int totalDuration;
    private List<T> content;

    public Totals(List<T> content, int totalDuration) {
        this.content = content;
        this.totalDuration = totalDuration;
    }

    public int getPageDuration() {
        return content
                .parallelStream()
                .filter(timed -> timed.getDuration() != null)
                .mapToInt(Timed::getDuration)
                .sum();
    }

    public int getOtherDuration() {
        return totalDuration - getPageDuration();
    }

    public int getTotalDuration() {
        return totalDuration;
    }

}
