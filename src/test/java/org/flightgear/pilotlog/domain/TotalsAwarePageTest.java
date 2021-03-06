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

import org.flightgear.pilotlog.dto.Total;
import org.flightgear.pilotlog.dto.TotalsAwarePage;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("javadoc")
public class TotalsAwarePageTest {

    @Test
    public void testTotalsAwarePage() {
        // Given some content
        List<String> content = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M");

        // and a pageable
        Pageable pageable = PageRequest.of(0, 10);

        // and a total
        Total total = mock(Total.class);
        when(total.getPageTotal()).thenReturn(10);
        when(total.getOtherTotal()).thenReturn(90);
        when(total.getTotal()).thenReturn(100);

        // and a totals map with that total added to it
        Map<String, Total> totals = new HashMap<>();
        totals.put("total", total);

        // and a totals aware page populated with that content, pageable and totals
        TotalsAwarePage<String> page = new TotalsAwarePage<>(content, pageable, content.size(), totals);

        // expect the content to be the same
        assertThat(page.getContent()).isEqualTo(content);

        // and the aggregated pageable information to be correct
        assertThat(page.getTotalElements()).isEqualTo(content.size());
        assertThat(page.getSize()).isEqualTo(pageable.getPageSize());
        assertThat(page.getNumber()).isEqualTo(pageable.getPageNumber());

        // and the page totals for "total" to be correct
        assertThat(page.getTotals().get("total").getTotal()).isEqualTo(100);
        assertThat(page.getTotals().get("total").getPageTotal()).isEqualTo(10);
        assertThat(page.getTotals().get("total").getOtherTotal()).isEqualTo(90);
    }

}
