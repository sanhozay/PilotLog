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

import net.sf.beanrunner.BeanRunner;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("javadoc")
public class AircraftTest {

    @Test
    public void testBean() throws Exception {
        BeanRunner beanRunner = new BeanRunner();
        beanRunner.testBean(new Aircraft());
    }

    @Test
    public void testCompleteConstructor() {
        // Given an aircraft constructed with the complete constructor
        Aircraft a = new Aircraft(
                "A", new Date(0L),
                0.0f, 1.0f, 0.5, // Distance
                0.5f, 1.5f, 1.0, // Fuel rate
                0, 3, 1.5,       // Speed
                1000.0, 1500L, 10L, 2000.0
        );
        // expect the accessors to reflect the values that were set
        assertThat(a.getModel()).isEqualTo("A");
        assertThat(a.getLast().toInstant().getEpochSecond()).isEqualTo(0L);
        assertThat(a.getDistance().getMin()).isEqualTo(0.0f);
        assertThat(a.getDistance().getMax()).isEqualTo(1.0f);
        assertThat(a.getDistance().getAvg()).isEqualTo(0.5);
        assertThat(a.getFuelRate().getMin()).isEqualTo(0.5f);
        assertThat(a.getFuelRate().getMax()).isEqualTo(1.5f);
        assertThat(a.getFuelRate().getAvg()).isEqualTo(1.0);
        assertThat(a.getSpeed().getMin()).isEqualTo(0);
        assertThat(a.getSpeed().getMax()).isEqualTo(3);
        assertThat(a.getSpeed().getAvg()).isEqualTo(1.5);
        assertThat(a.getTotalDistance()).isEqualTo(1000.0);
        assertThat(a.getTotalDuration()).isEqualTo(1500L);
        assertThat(a.getTotalFlights()).isEqualTo(10L);
        assertThat(a.getTotalFuel()).isEqualTo(2000.0);
    }

    @Test
    public void testModelConstructor() {
        // Given an aircraft constructed with a model only
        Aircraft a = new Aircraft("A");
        // expect the model to be as configured
        assertThat(a.getModel()).isEqualTo("A");
    }

    @Test
    @SuppressWarnings("all")
    public void testEqualsSame() {
        // Given two identical aircraft
        Aircraft a1 = new Aircraft("A");
        Aircraft a2 = new Aircraft("A");
        // expect the usual equatable rules to apply
        assertThat(a1.equals(null)).isFalse();
        assertThat(a1.equals("")).isFalse();
        assertThat(a1.equals(a1)).isTrue();
        // and the aircraft to be equal
        assertThat(a1).isEqualTo(a2);
    }

    @Test
    public void testEqualsDifferent() {
        // Given two different aircraft
        Aircraft a1 = new Aircraft("A");
        Aircraft a2 = new Aircraft("B");
        // expect them to be different
        assertThat(a1).isNotEqualTo(a2);
    }

    @Test
    public void testHashCodeSame() {
        // Given two identical aircraft
        Aircraft a1 = new Aircraft("A");
        Aircraft a2 = new Aircraft("A");
        // expect their hash codes to be the same
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
    }

    @Test
    public void testHashCodeDifferent() {
        // Given two different aircraft
        Aircraft a1 = new Aircraft("A");
        Aircraft a2 = new Aircraft("B");
        // expect their hash codes to be different
        assertThat(a1.hashCode()).isNotEqualTo(a2.hashCode());
    }

}
