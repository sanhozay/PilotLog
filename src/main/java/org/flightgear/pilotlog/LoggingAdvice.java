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

package org.flightgear.pilotlog;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Logging advice bean.
 *
 * @author Richard Senior
 */
@Aspect
@Component
@Profile("profiling")
public class LoggingAdvice {

    private final Logger log = LoggerFactory.getLogger(LoggingAdvice.class);

    @Pointcut("execution(* org.flightgear.pilotlog.service.*Service.*(..))")
    public void serviceMethods() {}

    @Before("serviceMethods()")
    public void logServiceCalls(JoinPoint joinPoint) {
        log.info(joinPoint.toShortString());
        for (Object arg : joinPoint.getArgs()) {
            log.info(" - {}", arg.toString());
        }
    }

}
