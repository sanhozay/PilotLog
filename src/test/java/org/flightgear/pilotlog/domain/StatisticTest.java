package org.flightgear.pilotlog.domain;

import net.sf.beanrunner.BeanRunner;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class StatisticTest {

    @Test
    public void testBean() throws Exception {
        BeanRunner beanRunner = new BeanRunner();
        beanRunner.addTestValue(Number.class, 10);
        beanRunner.testBean(new Statistic<Integer>());
    }

}
