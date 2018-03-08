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
public class TrackPointTest {

    @Test
    public void testBean() throws Exception {
        BeanRunner beanRunner = new BeanRunner();
        beanRunner.testBean(new TrackPoint());
    }

    @Test
    public void testConstructor() {
        // Given some track parameters
        float altitude = 100.0f, fuel = 1000.0f, odometer = 0.0f;
        Coordinate coordinate = new Coordinate(51.0f, 1.0f);
        // and a trackpoint constructed with the complete constructor
        TrackPoint trackPoint = new TrackPoint(altitude, fuel, odometer, coordinate);
        // expect the latitude and longitude to be correct
        assertThat(trackPoint.getAltitude()).isEqualTo(altitude);
        assertThat(trackPoint.getFuel()).isEqualTo(fuel);
        assertThat(trackPoint.getOdometer()).isEqualTo(odometer);
        assertThat(trackPoint.getCoordinate()).isEqualTo(coordinate);
    }

    @Test
    @SuppressWarnings("all")
    public void testEqualsSame() {
        // Given two identical track points
        TrackPoint p1 = new TrackPoint();
        TrackPoint p2 = new TrackPoint();
        // expect the usual equatable rules to apply
        assertThat(p1.equals(null)).isFalse();
        assertThat(p1.equals("")).isFalse();
        assertThat(p1.equals(p1)).isTrue();
        assertThat(p1).isEqualTo(p2);
    }

    @Test
    public void testEqualsDifferent() {
        // Given two different track points
        TrackPoint p1 = new TrackPoint();
        p1.setTimestamp(new Date(0L));
        TrackPoint p2 = new TrackPoint();
        p2.setTimestamp(new Date(1L));
        // expect them not to be equal
        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    public void testHashCodeSame() {
        // Given two identical track points
        TrackPoint p1 = new TrackPoint();
        TrackPoint p2 = new TrackPoint();
        // expect their hash codes to be equal
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    public void testHashCode() {
        // Given two different track points
        TrackPoint p1 = new TrackPoint();
        p1.setTimestamp(new Date(0L));
        TrackPoint p2 = new TrackPoint();
        p2.setTimestamp(new Date(1L));
        // expect their hash codes to be different
        assertThat(p1.hashCode()).isNotEqualTo(p2.hashCode());
    }

}
