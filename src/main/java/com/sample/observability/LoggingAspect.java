package com.sample.observability;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// ...existing imports...
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
    private final Tracer tracer;

    public LoggingAspect() {
        OpenTelemetry openTelemetry = OpenTelemetrySdk.builder().build();
        this.tracer = openTelemetry.getTracer("com.sample.observability.LoggingAspect");
    }

    // For testing
    public LoggingAspect(Tracer tracer) {
        this.tracer = tracer;
    }



   

    // Pointcut for all methods in com.sample.observability package
    @Pointcut("within(com.sample.observability..*)")
    public void applicationPackagePointcut() {
        // Method is empty as this is just a Pointcut declaration
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        Span span = tracer.spanBuilder(className + "." + methodName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            logger.info("Entering method: {}.{}()", className, methodName);
            Object result = joinPoint.proceed(); // Continue to the advised method execution
            logger.info("Exiting method: {}.{}() with result: {}", className, methodName, result);
            return result;
        } catch (IllegalArgumentException e) {
            span.recordException(e);
            span.setAttribute("error.message", "Illegal argument in " + className + "." + methodName);
            logger.error("Illegal argument: {} in {}.{}()", e.getMessage(), className, methodName);
            throw e;
        } finally {
            span.end();
        }
    }
}
