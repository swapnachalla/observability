# Spring Boot Observability Application - Getting Started

This is a comprehensive Spring Boot application demonstrating modern observability practices including distributed tracing with OpenTelemetry, aspect-oriented programming (AOP) for logging, and metrics collection.

## 🚀 Quick Start

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **OpenTelemetry Collector** (optional, for full observability stack)

### Running the Application

1. **Build and run with Maven:**

   ```bash
   mvn clean package
   mvn spring-boot:run
   ```

2. **Access the application:**
   - **REST API**: `http://localhost:8080`
   - **H2 Database Console**: `http://localhost:8080/h2-console`
   - **Actuator Endpoints**: `http://localhost:8080/actuator`

## 🏗️ Application Architecture

### Core Components

#### **🗂️ Data Layer**

- **Entity**: `ApplicationUser` - JPA entity with id, name, email, and activeFlag
- **Repository**: `UserRepository` - Spring Data REST repository with custom query methods
- **Database**: H2 in-memory database for development and testing

#### **🎯 REST Endpoints**

Auto-generated REST endpoints via Spring Data REST:

- `GET /users` - List all users with pagination
- `GET /users/search/findByName?name={name}` - Find users by name
- `GET /users/search/findByEmail?email={email}` - Find users by email
- `GET /users/search/findByActiveFlag?activeFlag={true|false}` - Find active/inactive users

#### **📊 Observability Layer**

- **LoggingAspect**: AOP-based interceptor for method entry/exit logging
- **OpenTelemetry Integration**: Distributed tracing with automatic span creation
- **Micrometer Metrics**: Application metrics exported via OTLP
- **Spring Boot Actuator**: Health checks and management endpoints

## 🔍 Observability Features

### **Distributed Tracing**

- **OpenTelemetry SDK 1.32.0** integration
- **Automatic span creation** for all service methods
- **Exception tracking** with detailed error information
- **Custom attributes** for business context
- **OTLP export** to OpenTelemetry Collector (endpoint: `http://localhost:4318`)

### **Structured Logging**

- **SLF4J** with comprehensive method interceptors
- **Correlation IDs** for request tracking
- **Exception logging** with full stack traces
- **Performance metrics** (execution time tracking)

### **Metrics Collection**

- **Micrometer registry** with OTLP export
- **JVM metrics** (heap, threads, GC)
- **HTTP request metrics** (latency, throughput, errors)
- **Custom business metrics** via AOP aspects
- **10-second export interval** to OTLP endpoint

### **Health Monitoring**

- **Spring Boot Actuator** endpoints
- **Database connectivity** checks
- **Application readiness** and liveness probes
- **Custom health indicators**

## 🧪 Testing

### **Running Tests**

```bash
# Run all tests
mvn test

# Run specific test classes
mvn test -Dtest=LoggingAspectTest
mvn test -Dtest=UserRepositoryTests
mvn test -Dtest=ObservabilityApplicationTests
```

### **Test Coverage**

- **Unit Tests**: `LoggingAspectTest` - AOP logging behavior
- **Integration Tests**: `UserRepositoryTests` - JPA repository functionality
- **Application Tests**: `ObservabilityApplicationTests` - Spring context loading

## 📋 Configuration

### **Application Properties**

Key configuration in `application.properties`:

```properties
# Application Identity
spring.application.name=observability

# OpenTelemetry Tracing
management.tracing.sampling.probability=1.0
management.otlp.tracing.endpoint=http://localhost:4318/v1/traces

# Metrics Export
management.otlp.metrics.export.enabled=true
management.otlp.metrics.export.step=PT10S
management.otlp.metrics.endpoint=http://localhost:4318/v1/metrics

# H2 Database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
```

### **OpenTelemetry Collector Setup** (Optional)

For complete observability stack, run OpenTelemetry Collector:

```yaml
# otel-collector-config.yaml
receivers:
  otlp:
    protocols:
      grpc:
        endpoint: 0.0.0.0:4317
      http:
        endpoint: 0.0.0.0:4318

exporters:
  logging:
    loglevel: debug
  jaeger:
    endpoint: jaeger:14250
    tls:
      insecure: true

service:
  pipelines:
    traces:
      receivers: [otlp]
      exporters: [logging, jaeger]
    metrics:
      receivers: [otlp]
      exporters: [logging]
```

## 🔧 Development

### **Key Dependencies**

- **Spring Boot 2.7.18** - Core framework
- **Spring Data JPA** - Data access layer
- **Spring Data REST** - Auto-generated REST APIs
- **Spring Boot AOP** - Aspect-oriented programming
- **OpenTelemetry 1.32.0** - Distributed tracing
- **Micrometer OTLP** - Metrics export
- **H2 Database** - In-memory database
- **JUnit 5** - Testing framework

### **Architecture Patterns**

- **Hexagonal Architecture** - Clean separation of concerns
- **Aspect-Oriented Programming** - Cross-cutting concerns (logging, tracing)
- **Repository Pattern** - Data access abstraction
- **Dependency Injection** - Spring IoC container
- **RESTful APIs** - HTTP-based service interfaces

## 📖 Reference Documentation

### **Spring Boot & Data**

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Data REST](https://docs.spring.io/spring-data/rest/docs/current/reference/html/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.7.18/actuator-api/html/)

### **Observability**

- [OpenTelemetry Java](https://opentelemetry.io/docs/instrumentation/java/)
- [Micrometer Documentation](https://micrometer.io/docs)
- [OTLP Specification](https://opentelemetry.io/docs/reference/specification/protocol/otlp/)
- [Spring Boot Tracing](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/actuator.html#actuator.tracing)

### **Testing & Quality**

- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/2.7.18/reference/html/spring-boot-features.html#boot-features-testing)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)

## 🎯 Use Cases

This application demonstrates:

- **Microservices observability** patterns
- **Production-ready monitoring** setup
- **Distributed tracing** implementation
- **Performance monitoring** best practices
- **Error tracking** and debugging capabilities
- **Compliance and audit** trail logging

## 🔗 Additional Resources

- **Sequence Diagram**: See `sequence-diagram.md` for detailed request flow visualization
- **Maven Documentation**: [Apache Maven](https://maven.apache.org/guides/index.html)
- **Docker Support**: Can be containerized with Spring Boot's built-in Docker support
