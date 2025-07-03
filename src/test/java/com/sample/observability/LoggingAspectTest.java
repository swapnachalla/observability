package com.sample.observability;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// ...existing imports...
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;

import static org.mockito.Mockito.*;

class LoggingAspectTest {
    private LoggingAspect loggingAspect;
    private OpenTelemetry openTelemetry;
    private Tracer tracer;
    private ProceedingJoinPoint joinPoint;
    private Span span;
    private Scope scope;

    @BeforeEach
    void setUp() {
        openTelemetry = mock(OpenTelemetry.class);
        tracer = mock(Tracer.class);
        span = mock(Span.class);
        scope = mock(Scope.class);
        joinPoint = mock(ProceedingJoinPoint.class);

        // Properly mock SpanBuilder and its startSpan method
        io.opentelemetry.api.trace.SpanBuilder spanBuilder = mock(io.opentelemetry.api.trace.SpanBuilder.class);
        when(tracer.spanBuilder(anyString())).thenReturn(spanBuilder);
        when(spanBuilder.startSpan()).thenReturn(span);
        when(span.makeCurrent()).thenReturn(scope);

        loggingAspect = new LoggingAspect(tracer);
    }

    @Test
    void testLogAround_NormalExecution() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(new org.aspectj.lang.Signature() {
            public String toShortString() { return "testMethod()"; }
            public String toLongString() { return "public void testMethod()"; }
            public String getName() { return "testMethod"; }
            public int getModifiers() { return 0; }
            public Class<?> getDeclaringType() { return LoggingAspectTest.class; }
            public String getDeclaringTypeName() { return "com.sample.observability.LoggingAspectTest"; }
        });
        when(joinPoint.proceed()).thenReturn("result");

        Object result = loggingAspect.logAround(joinPoint);
        verify(span).end();
        assert result.equals("result");
    }

    @Test
    void testLogAround_Exception() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(new org.aspectj.lang.Signature() {
            public String toShortString() { return "testMethod()"; }
            public String toLongString() { return "public void testMethod()"; }
            public String getName() { return "testMethod"; }
            public int getModifiers() { return 0; }
            public Class<?> getDeclaringType() { return LoggingAspectTest.class; }
            public String getDeclaringTypeName() { return "com.sample.observability.LoggingAspectTest"; }
        });
        when(joinPoint.proceed()).thenThrow(new IllegalArgumentException("bad arg"));

        try {
            loggingAspect.logAround(joinPoint);
        } catch (IllegalArgumentException e) {
            verify(span).recordException(e);
            verify(span).setAttribute(eq("error.message"), anyString());
            verify(span).end();
        }
    }
}
