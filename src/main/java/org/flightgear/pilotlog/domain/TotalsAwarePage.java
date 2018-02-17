package org.flightgear.pilotlog.domain;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

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
