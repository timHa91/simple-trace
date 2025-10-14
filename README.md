# simple-trace

A lightweight distributed tracing tool for microservices written in Java.

## What is this?

simple-trace helps you understand request flows across multiple services by automatically tracking requests with Trace IDs and building parent-child span hierarchies. It visualizes the entire request journey through your distributed system.

**Key Features:**
- Automatic trace propagation via HTTP headers
- Hierarchical span tree reconstruction
- Visual request flow analysis
- Orphaned span detection
- Error tracking with status codes and messages

## Quick Start

### Prerequisites
- Java 21
- Maven

### 1. Start the Tracer Backend
- cd tracer-backend
- mvn spring-boot:run
- Runs on http://localhost:8080

### 2. Start the Demo Services
**Terminal 1:**
- cd service-a
- mvn spring-boot:run # Port 8081

**Terminal 2:**
- cd service-b
- mvn spring-boot:run # Port 8082

**Terminal 3:**

- cd service-c
- mvn spring-boot:run # Port 8083

### 3. Test the Flow
Trigger a request through Service A → B → C

- curl http://localhost:8081/api/start
Get the trace (use the trace-id from logs or response headers)

- curl http://localhost:8080/api/traces/{trace-id}/tree

### Example Output
```
Service-A [inbound] GET /api/start (215ms)
  Service-A [outbound] GET /api/process (209ms)
    Service-B [inbound] GET /api/process (181ms)
      Service-B [outbound] GET /api/work (31ms)
        Service-C [inbound] GET /api/work (19ms)
```

## 📦 Components

### tracer-backend
Central trace collector that receives spans from all services, reconstructs the trace hierarchy, and provides visualization endpoints.

**Endpoints:**
- `POST /api/traces` - Receive spans from services
- `GET /api/traces/{id}` - Get trace as JSON with full hierarchy
- `GET /api/traces/{id}/tree` - Get trace as human-readable tree

### tracer-library
Shared library containing the tracing logic that can be integrated into any Spring Boot service:
- `TraceRequestFilter` - Captures inbound HTTP requests
- `RestTemplate` Interceptor - Captures outbound HTTP calls
- `TraceContext` - ThreadLocal context for trace/span IDs
- `TraceDTO` - Data transfer object for spans

### service-a, service-b, service-c
Demo microservices showing distributed tracing in action:
- **Service A** (`/api/start`) - Entry point, calls Service B
- **Service B** (`/api/process`) - Intermediate service, calls Service C
- **Service C** (`/api/work`) - Final service in the chain

## 🔍 How it works

1. **Trace Initialization**: The first service generates a `Trace-ID` and propagates it via the `X-trace-id` HTTP header
2. **Span Creation**: Each service creates spans for:
    - **Inbound**: Incoming HTTP requests (captured by Filter)
    - **Outbound**: Outgoing HTTP calls (captured by RestTemplate Interceptor)
3. **Parent-Child Linking**: Each outbound span becomes the parent of the next service's inbound span via the `X-parent-span-id` header
4. **Async Export**: Spans are asynchronously sent to the tracer-backend
5. **Tree Reconstruction**: The backend builds the full trace hierarchy using `spanId` and `parentSpanId` relationships

## 🛠️ Tech Stack

- Java 21
- Spring Boot 3.5.x
- Maven
- ThreadLocal for context propagation
- Async span export

## 🚧 Status

**Work in Progress - MVP Phase**

Current features are functional but intended for learning and prototyping.

## 📝 Roadmap

- [ ] Metrics with Micrometer (traces/sec, error rates, latency percentiles)
- [ ] Configurable sampling strategies
- [ ] Persistence layer (PostgreSQL/MongoDB)
- [ ] Kafka integration for robust span transport
- [ ] Web UI with waterfall charts and service maps
- [ ] Docker Compose setup
- [ ] Extract tracer-library as standalone dependency

## 📄 License

MIT License - Feel free to use and modify!

---

**Built as a learning project to understand distributed tracing systems like Jaeger and Zipkin.**
