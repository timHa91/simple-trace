# simple-trace

A lightweight distributed tracing tool for microservices written in Java.

[![Release](https://img.shields.io/github/v/release/timHa91/simple-trace)](https://github.com/timHa91/simple-trace/releases)
[![Build Status](https://img.shields.io/github/actions/workflow/status/timHa91/simple-trace/maven.yml?branch=main)](https://github.com/timHa91/simple-trace/actions)
[![License](https://img.shields.io/github/license/timHa91/simple-trace)](LICENSE)
[![Java](https://img.shields.io/badge/java-21-blue)](https://adoptium.net/temurin/releases/?version=21)

---

## What is this?

simple-trace helps you understand request flows across multiple services by automatically tracking requests with 
Trace IDs and building parent-child span hierarchies. It visualizes the entire request journey through your distributed system.

**Perfect for:**
- Debugging microservice issues
- Understanding service dependencies
- Identifying performance bottlenecks
- Learning distributed tracing concepts

**Key Features:**
- ✅ Automatic trace propagation via HTTP headers
- ✅ Hierarchical span tree reconstruction
- ✅ Visual request flow analysis
- ✅ Orphaned span detection
- ✅ Error tracking with status codes and messages
- ✅ Metrics with Micrometer (traces/sec, error rates, latency percentiles)
- ✅ PostgreSQL persistence (spans survive restarts)
- ✅ Automated retention policy (7-day cleanup)
- ✅ Production-ready Docker Compose setup**

---

## 🚀 Quick Start

### Prerequisites
- Docker & Docker Compose
- (Optional) Java 21 + Maven for local development

### Database Configuration
Create `.env`:
- DB_HOST=db
- DB_PORT=5432
- DB_NAME=trace_db
- DB_USER=postgres
- DB_PASSWORD=postgres

### Start Everything
- git clone https://github.com/yourusername/simple-trace
- cd simple-trace
- docker compose up -d

**What starts:**
- PostgreSQL (persistent trace storage)
- tracer-backend (span collector on port 8080)
- service-a, service-b, service-c (demo services on ports 8081-8083)

### Test the Flow

**1. Trigger a distributed request:**
- curl http://localhost:8081/api/start

**2. Get the trace tree** (use trace-id from response or logs):
- curl http://localhost:8080/api/traces/{trace-id}/tree

**3. Example Output:**
```
Service-A [inbound] GET /api/start (215ms)
  Service-A [outbound] GET /api/process (209ms)
    Service-B [inbound] GET /api/process (181ms)
      Service-B [outbound] GET /api/work (31ms)
        Service-C [inbound] GET /api/work (19ms)
```

**4. Check metrics:**
- curl http://localhost:8080/actuator/metrics/spans.received

---

## 🏗️ Architecture

### Current (v0.1.0): HTTP-based Transport
- Services → HTTP POST → tracer-backend → PostgreSQL

**Flow:**
1. Service creates span (inbound/outbound)
2. Span sent via HTTP to backend
3. Backend persists to PostgreSQL
4. Backend reconstructs trace tree on demand

### Coming (v0.2.0): Kafka-based Transport
Services → Kafka Topic → tracer-backend (consumer) → PostgreSQL

**Benefits:**
- Spans survive backend downtime
- Fire-and-forget (non-blocking)
- Batch processing support

---

## Components

### tracer-backend
Central trace collector that receives spans, reconstructs hierarchies, and provides visualization endpoints.

**Endpoints:**
- `POST /api/traces` - Receive spans from services
- `GET /api/traces/{id}` - Get trace as JSON with full hierarchy
- `GET /api/traces/{id}/tree` - Get trace as human-readable tree
- `GET /actuator/metrics` - Prometheus metrics (spans received, errors, latency)

---

## How it Works

1. **Trace Initialization**: The first service generates a `Trace-ID` and propagates it via the `X-trace-id` HTTP header
2. **Span Creation**: Each service creates spans for:
   - **Inbound**: Incoming HTTP requests (captured by Filter)
   - **Outbound**: Outgoing HTTP calls (captured by RestTemplate Interceptor)
3. **Parent-Child Linking**: Each outbound span becomes the parent of the next service's inbound span via the `X-parent-span-id` header
4. **Async Export**: Spans are asynchronously sent to the tracer-backend
5. **Persistence**: Backend stores spans in PostgreSQL
6. **Tree Reconstruction**: Backend builds the full trace hierarchy using `spanId` and `parentSpanId` relationships
7. **Retention**: Scheduled job removes spans older than 7 days

---

## Tech Stack

- **Java 21** (Eclipse Temurin)
- **Spring Boot 3.5.x**
- **PostgreSQL 15** (persistence)
- **Docker & Docker Compose** (deployment)
- **JPA / Hibernate** (ORM)
- **Micrometer** (metrics)
- **ThreadLocal** (context propagation)
- **Testcontainers** (integration tests)
- **JUnit 5 + AssertJ** (testing)

---

## 🚧 Status

**Version 0.1.0 - Production-Ready MVP**

## Acknowledgments

Built as a learning project to understand distributed tracing systems like **Jaeger** and **Zipkin**.

**Inspired by:**
- Jaeger (CNCF project)
- Zipkin (OpenZipkin)
- OpenTelemetry

---

## Contact

Questions or feedback? Open an issue or reach out!

**⭐ If this project helped you, consider giving it a star on GitHub!**

## License

MIT License - Feel free to use and modify!

---