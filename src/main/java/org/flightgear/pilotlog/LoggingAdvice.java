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

    Logger log = LoggerFactory.getLogger(LoggingAdvice.class);

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
