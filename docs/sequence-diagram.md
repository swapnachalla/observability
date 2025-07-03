%% Spring Boot Observability Application - Enhanced Sequence Diagram
%% This comprehensive sequence diagram illustrates the complete request flow through your Spring Boot
%% observability application, showcasing OpenTelemetry distributed tracing, AOP-based logging aspects,
%% and comprehensive error handling with observability features.

sequenceDiagram
autonumber

    box rgb(230, 240, 255) Client Layer
    participant Client as 🌐 Client
    end

    box rgb(240, 255, 240) Spring Boot Application
    participant Gateway as 🚪 API Gateway
    participant SpringBoot as 🍃 Spring Boot App
    participant Controller as 🎯 REST Controller
    end

    box rgb(255, 245, 230) Observability Layer
    participant LoggingAspect as 📊 Logging Aspect
    participant OpenTelemetry as 🔍 OpenTelemetry Tracer
    participant Metrics as 📈 Metrics Collector
    participant Logger as 📝 SLF4J Logger
    end

    box rgb(255, 240, 245) Data Layer
    participant UserRepository as 🗂️ User Repository
    participant Database as 🗄️ H2 Database
    end

    Note over Client,Database: 🔄 Successful Request Processing Flow with Full Observability

    Client->>+Gateway: HTTP GET /users/search/findByName?name=John
    Note right of Client: Client initiates user search

    Gateway->>+SpringBoot: Route request with tracing headers
    SpringBoot->>+Controller: Dispatch to findByName endpoint

    %% AOP Aspect intercepts the method call
    rect rgb(240, 248, 255)
    Note over Controller,LoggingAspect: 🎭 AOP Interception
    Controller->>+LoggingAspect: @Around advice intercepts method call
    LoggingAspect->>+OpenTelemetry: Create new span "findByName"
    OpenTelemetry-->>-LoggingAspect: Span created with trace ID

    LoggingAspect->>+Metrics: Increment method invocation counter
    Metrics-->>-LoggingAspect: Counter updated

    LoggingAspect->>+Logger: Log method entry with parameters
    Logger-->>-LoggingAspect: Entry logged with trace ID
    end

    %% Proceed with actual method execution
    rect rgb(245, 255, 245)
    Note over LoggingAspect,Database: 💾 Data Access Layer
    LoggingAspect->>+UserRepository: proceed() - Execute findByName("John")
    UserRepository->>+OpenTelemetry: Create database span
    OpenTelemetry-->>-UserRepository: DB span active

    UserRepository->>+Database: SELECT * FROM application_user WHERE name = ?
    Note right of Database: Query execution with parameters
    Database-->>-UserRepository: ResultSet: [User{id=1, name="John", email="john@example.com"}]

    UserRepository->>OpenTelemetry: Add span attributes (rows=1, query_time=15ms)
    UserRepository-->>-LoggingAspect: List of ApplicationUser returned
    end

    %% Log successful execution and metrics
    rect rgb(240, 248, 255)
    Note over LoggingAspect,Metrics: 📊 Success Logging & Metrics
    LoggingAspect->>+Logger: Log successful execution with result count
    Logger-->>-LoggingAspect: Success logged

    LoggingAspect->>+Metrics: Record execution time and success metrics
    Metrics-->>-LoggingAspect: Metrics recorded

    LoggingAspect->>+OpenTelemetry: End span with success status
    OpenTelemetry-->>-LoggingAspect: Span completed successfully
    LoggingAspect-->>-Controller: Return List of ApplicationUser
    end

    Controller-->>-SpringBoot: HTTP 200 OK with JSON response
    SpringBoot-->>-Gateway: Response with observability headers
    Gateway-->>-Client: JSON: [{"id":1,"name":"John","email":"john@example.com"}]

    Note over Client,Database: ⚠️ Error Handling Flow with Comprehensive Observability

    Client->>+Gateway: HTTP GET /users/search/findByName?name=""
    Note right of Client: Client sends invalid empty name

    Gateway->>+SpringBoot: Route request with correlation ID
    SpringBoot->>+Controller: Dispatch to findByName endpoint

    Controller->>+LoggingAspect: @Around advice intercepts
    LoggingAspect->>+OpenTelemetry: Create span with correlation ID
    OpenTelemetry-->>-LoggingAspect: Error tracking span created

    LoggingAspect->>+Logger: Log method entry with empty parameter
    Logger-->>-LoggingAspect: Warning logged for empty input

    LoggingAspect->>+UserRepository: proceed() with invalid argument

    rect rgb(255, 235, 235)
    Note over UserRepository,Database: 🚨 Exception Handling
    UserRepository->>UserRepository: Validate input parameter
    UserRepository-->>-LoggingAspect: IllegalArgumentException("Name cannot be empty")

    LoggingAspect->>+OpenTelemetry: Record exception on span
    Note right of OpenTelemetry: Span marked as error<br/>Exception details captured
    OpenTelemetry-->>-LoggingAspect: Exception recorded

    LoggingAspect->>+Logger: Log error with full stack trace
    Logger-->>-LoggingAspect: Error logged with correlation ID

    LoggingAspect->>+Metrics: Increment error counter
    Metrics-->>-LoggingAspect: Error metrics updated

    LoggingAspect->>+OpenTelemetry: End span with error status
    OpenTelemetry-->>-LoggingAspect: Span completed with error

    LoggingAspect-->>Controller: Re-throw IllegalArgumentException
    end

    Controller-->>-SpringBoot: Exception bubbles up
    SpringBoot-->>-Gateway: HTTP 400 Bad Request
    Gateway-->>-Client: JSON: {"error":"Name cannot be empty","timestamp":"2025-07-03T10:30:00Z"}

    Note over LoggingAspect,Metrics: 🔧 Observability Infrastructure Features
    Note right of LoggingAspect: 📋 AOP Capabilities:<br/>• Method entry/exit logging<br/>• Execution time measurement<br/>• Parameter validation logging<br/>• Exception handling & correlation

    Note right of OpenTelemetry: 🔍 Distributed Tracing:<br/>• End-to-end request tracking<br/>• Span creation & management<br/>• Exception recording<br/>• Custom attributes & baggage<br/>• Cross-service correlation

    Note right of Metrics: 📊 Metrics Collection:<br/>• Request counters<br/>• Response time histograms<br/>• Error rate tracking<br/>• Custom business metrics<br/>• SLA/SLO monitoring

%% Design Art API : https://kroki.io/mermaid/svg/eNqtWNtuG0UYvu9TjFZKlILduEkjkEVa3MRpU9Im2IZKIFSNdyf20PXOdmY2B1WVEPQCAS2IVkKqhEDcgJAQt1zxMH0BeAT-f2b26I3tlFbVKt79z8dvZmmJ9GPJoxG5LoQm-0PF5BEd8pDrU9KJ45D7VHMRkSbpRmMa-SwgffYgYfAX2eZ0JOnkwtISGYy5Ir6YxJKNWaT4ESMqJQssGeFhmCgtqWaK6DEz5CHTjEikVJochuIYvkiRjMbkVCSyZBuqESX7aG5fg6ixOPapQvL9mEUDFrIJ0_IU1INSPkw0mA7afaBokM7-QXNIFbwKxWiETFTFzNeqYfTQKKh4w6QUkkAEghCpj7keV6w5ZFQnkqlLFy6krqcBIvCPJlpEyWTIpPlpHkNxQuRouLK23mqQtSv42Ni4SLZCziJN9uipI46p1NznMYW37iNV5N-fvvnO_TRULApqRDupRv7FUkAL6Z3ScgOydExPrZoXv5POwW76borWyjQiDfmTL6pqpp0QkZYiDJm0LE__JL1uf1B4P8Mj6ww-1sGjcsmamDmGosI9m-WOSbLV-eyr9C2xr6eYynVkmJ4_qbwdQEXVZOk2fOS-cpq-zH5voXO-FrLWwjQcz34k_b2dK7fcy7mxMNUDhbNNNT2jbD6AKPVYLBQH5c6ZHz7_569vzReSf5riRKHYKo7nMfLcXMteZ8aZP-4I6GdxBBJtYTZSsjYG7zHpJ77PlDpMQlBpu_5ACnyFedjBAWBaaycJw3JqrXwrtXn16puuGtvk5mBwQG50B2Q1AXK1qhiV_nj1kEfB9dM7dMKuRfDYvCXGUW6i5KOxJuLQCWynbcUjrrmZUCiMWFl53J1S1J9XfZv0RFIYY8YBN2nImNEArDLcOQsKyGu9DZNUxVT7wCZIbjiGNRbctbd5wGyC0eUKFqzVTPos1nagQkWOBQwuGoaGWCJNPgSuvG3nSzVRmR2NUpdgxp7-YdTtpnrSUZHzoCcVtnc7MMFhgNLgiMP4LxhZNbDEiJJKvQVJkTBTGYnYMYH4RMTLY-MZASX6JkhoVmzpI5tvxAR5YhjZ3c5jOmWFa9Y2-A2sE6wLZzmPjoRbiD74qF2jOYY6A7YsGUniAG2YodX2ehs_pOpAM_SqMRs6ErzWaSlZ4jqFXcODW63W49IYgWoy3ZdSUl8nNMy0nzA_yVJeKKaNbKNMFVPJmFLzf_-3HU8dMwAKU2oqEOVR1SaxNXHlIoIQYxMrNMmKh53tWUPKrGdXVJDONCyrMyqpasb2dVuEECQABHkQp3Xmbve7e92tAXmD7PT2bxfxyj0zXu7e7Pa6BMcT2STXaqZTLun9hEFas5TUFkVKXWt-j6kk1H0GJfIxfnvIg83LDaN804awQdiE8nDT-xR-vctOKMKzSwCDvEefzHC3EuFOELg4aQe7FFmR4lihtgfoxT3NQefljYmqTVpNTe8BhMNwFBCL2VqSAeKKXFdVKxv7SOXrJg8dgruJ7dhzjMlyZWcjwuAIt9UyPLGcToT5nV5roUmuNAmzc2ZOz6f6bdcvMtl6zBey0OIEc2Ii4wwqRWjGfEuBjTQCZyqvFEo3coVivE3VKg0oWp1nuLtTRFAIZXhaYwEKKO7cnimes2pruqQKOw9FFfe_gSBrrRbZf896c6u_fwcTGIvIIaTC7kfuDL30HFHdiaKIHVLkYbxwmAW1QDc_9HjgtS83POxlr-2a2TPNDD_runkeVnv54meEeV1z6LmZHnpyeLZVOhu9VpzmefNRmoLEKFzGNORQxJMYgoXMrwLUoGwlC-1OdzvyNeC0VwNI5wFFeevUeDC3dWxmERncx8yqAkx6ZYhi85DtpDkz6y6VEap2SOUQzLECeBQneoYRZ8MDY0VaFVSOkgkr5iSf8wa8rNtHdc6XxZcgzIvfAH84GJx1Rf1SrBr5IdqEaTPeVYI0fwHuQiWNaNhxPmVWrHim_nwaRXDSHjIbQgeGFqqmbBGkfsH_DBSVm7DCaabvhMr7kD04GpoLkneGcvVqHqOAaZhBCuyL8WokWLA2M_4FlkqxJO0djakCXAK4S_z7Fv7OA82Gswiaa5pq4cOCtWPBA4JV7bbtAseEmVvUav4fOzSXcpYJIKO8RZt4Y3d8Zomea5HmqR8mw2GIx_B45vo0S-UKbN7rNEhvFObsy4ee8Q82Y23vNDyEQhDCSQwka621jWbrrWZrfXC51V5vtVutj7xHFxbFhs9_rdxP7UaHkiotEx8bguy4O8Oabps6iz_72pzFt2hshXGm2qbfXn72CxZYNo1X2QnX6b1mRtEtQ70Joyqx9ZqRHKRTiRzZcYXU03LSFGWXocvFbsnzPHN6mKu07cLV7MBemOQuQWU3tWhC6WQLO91XGU1-vEeDlmEcRXRUdqo6TYrsW4nSYlI8qSyTIR2NQEZOI4VSTcwh7uoFHC0fDSoXf8CXe5jegLlJoQofHCg0qRpDlATeI-cEdmjgVfp0TJxTw0TxqADk85jtdVb7e_tkIiLcN8D6H_SnMSQ
