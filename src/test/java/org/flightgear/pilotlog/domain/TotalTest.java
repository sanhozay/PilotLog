package org.flightgear.pilotlog.domain;

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
