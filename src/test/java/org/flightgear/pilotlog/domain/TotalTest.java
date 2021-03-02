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
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("javadoc")
public class TotalTest {

    @Test
    public void testOtherTotalInteger() {
        Total<Integer> total = new Total<>(10, 100);
        assertThat(total.getOtherTotal()).isEqualTo(90);
    }

    @Test
    public void testOtherTotalLong() {
        Total<Long> total = new Total<>(10L, 100L);
        assertThat(total.getOtherTotal()).isEqualTo(90);
    }

    @Test
    public void testOtherTotalFloat() {
        Total<Float> total = new Total<>(10.0f, 100.0f);
        assertThat(total.getOtherTotal()).isEqualTo(90.0f);
    }

    @Test
    public void testOtherTotalDouble() {
        Total<Double> total = new Total<>(10.0, 100.0);
        assertThat(total.getOtherTotal()).isEqualTo(90.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalType() {
        Total<Short> total = new Total<>((short)10, (short)100);
        total.getOtherTotal();
    }

    @Test
    public void testPageTotal() {
        Total<Integer> total = new Total<>(10, 100);
        assertThat(total.getPageTotal()).isEqualTo(10);
    }

    @Test
    public void testGrandTotal() {
        Total<Integer> total = new Total<>(10, 100);
        assertThat(total.getTotal()).isEqualTo(100);
    }

}
