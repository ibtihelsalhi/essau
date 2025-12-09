# Prestify Platform - Technical Architecture

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Diagram](#architecture-diagram)
3. [Component Description](#component-description)
4. [Data Flow](#data-flow)
5. [Technology Stack](#technology-stack)
6. [Deployment Architecture](#deployment-architecture)
7. [Security Architecture](#security-architecture)
8. [Scalability Strategy](#scalability-strategy)

---

## System Overview

Prestify is a comprehensive service management platform built on a microservices-ready architecture with integrated monitoring and logging. The system is containerized using Docker and orchestrated with Docker Compose, deployable to AWS EC2 with full observability.

**Key Characteristics:**
- **Type**: Web Application (Service Platform)
- **Architecture Pattern**: Monolithic (scalable to microservices)
- **Data Storage**: Relational Database (MySQL)
- **Protocol**: REST API + HTTP
- **Monitoring**: Prometheus + Grafana + Loki
- **Deployment**: Docker + AWS EC2

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         CLIENT LAYER                                │
│  ┌──────────────────────────────────────────────────────────────┐   │
│  │  Web Browser / Mobile App / REST Client                      │   │
│  └──────────────────┬───────────────────────────────────────────┘   │
└─────────────────────┼───────────────────────────────────────────────┘
                      │ HTTP/HTTPS
                      ▼
┌─────────────────────────────────────────────────────────────────────┐
│                  AWS EC2 / DOCKER HOST                              │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │            DOCKER NETWORK (prestify_network)                  │  │
│  │                                                               │  │
│  │  ┌──────────────────────┐  ┌──────────────────────────────┐  │  │
│  │  │  PRESTIFY APP        │  │  MYSQL DATABASE            │  │  │
│  │  ├──────────────────────┤  ├──────────────────────────────┤  │  │
│  │  │ • Spring Boot 3.0    │  │ • MySQL 8.0                │  │  │
│  │  │ • Actuator (Metrics) │  │ • Database Schema         │  │  │
│  │  │ • Port: 8080         │  │ • Port: 3306              │  │  │
│  │  │ • Health Check ✓     │  │ • Health Check ✓          │  │  │
│  │  │ • JVM Tuned          │  │ • Persistent Volume       │  │  │
│  │  │                      │◄─┤ • Backup Ready            │  │  │
│  │  │ Controllers:         │  └──────────────────────────────┘  │  │
│  │  │ • User Management    │                                      │  │
│  │  │ • Offers             │  ┌──────────────────────────────┐  │  │
│  │  │ • Categories         │  │  PROMETHEUS                  │  │  │
│  │  │ • Reclamations       │  ├──────────────────────────────┤  │  │
│  │  │                      │  │ • Time Series DB           │  │  │
│  │  └─────────────────────┬┤  │ • Scrapes Metrics          │  │  │
│  │                        │   │ • Retention: 30 days       │  │  │
│  │                        │   │ • Port: 9090               │  │  │
│  │  ┌────────────────────▼┘  │ • Persistent Volume        │  │  │
│  │  │ Application Metrics    │ └────────┬───────────────────┘  │  │
│  │  │ • HTTP Requests        │          │                      │  │
│  │  │ • JVM Memory           │          ▼                      │  │
│  │  │ • Database Queries     │  ┌──────────────────────────┐   │  │
│  │  │ • Threads             │  │  GRAFANA               │   │  │
│  │  │ • Cache Performance    │  ├──────────────────────────┤   │  │
│  │  └────────────────────────┘  │ • Dashboards           │   │  │
│  │                             │ • Alerts               │   │  │
│  │  ┌──────────────────────┐   │ • Port: 3000           │   │  │
│  │  │  LOKI                │   │ • Persistent Volume    │   │  │
│  │  ├──────────────────────┤   │ • Multi-datasource     │   │  │
│  │  │ • Log Aggregation    │   │ • Custom Dashboards    │   │  │
│  │  │ • Distributed Logs   │   └──────────────────────────┘   │  │
│  │  │ • Port: 3100         │                                   │  │
│  │  │ • Persistent Volume  │   ┌──────────────────────────┐   │  │
│  │  │ • 720h Retention     │   │  PROMTAIL              │   │  │
│  │  └──────────┬───────────┘   ├──────────────────────────┤   │  │
│  │             │                │ • Log Collection       │   │  │
│  │             └───────────────▶│ • Multiline Support    │   │  │
│  │                             │ • JSON Parsing         │   │  │
│  │                             └──────────────────────────┘   │  │
│  │                                                            │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │            VOLUMES (Persistent Data)                    │   │
│  │  • mysql_data (Database)                                │   │
│  │  • prometheus_data (Metrics)                            │   │
│  │  • loki_data (Logs)                                     │   │
│  │  • grafana_data (Configurations)                        │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              │ SSH (Deployment)
                              │
                    ┌─────────▼──────────┐
                    │  GitLab CI/CD      │
                    │  Pipeline Runner   │
                    └────────────────────┘
```

---

## Component Description

### 1. **Spring Boot Application (Prestify App)**

**Role**: Core business logic and API endpoints

**Technology**:
- Java 17 with Spring Boot 3.0
- Spring Data JPA for database access
- Spring Security for authentication
- Spring Web for REST APIs
- Micrometer for metrics

**Responsibilities**:
- User authentication and authorization
- Service offers management
- Categories management
- Reclamations/Claims handling
- Business logic implementation
- Metrics exposure for monitoring

**Endpoints**:
```
- POST /api/auth/login
- GET /api/offers
- POST /api/offers
- PUT /api/offers/{id}
- DELETE /api/offers/{id}
- GET /api/categories
- POST /api/reclamations
- GET /actuator/health
- GET /actuator/metrics
- GET /actuator/prometheus
```

**Scaling Approach**:
- Horizontal scaling: Run multiple instances behind load balancer
- Database connection pooling: HikariCP
- Caching: Spring Cache with optional Redis backend

---

### 2. **MySQL Database**

**Role**: Persistent data storage

**Configuration**:
- Version: 8.0 (LTS)
- Port: 3306
- Character Set: utf8mb4
- Collation: utf8mb4_unicode_ci

**Key Features**:
- User-level access control
- Prepared statements for security
- Connection pooling (20 max connections)
- Automated backups
- Health monitoring

**Database Schema**:
```
Tables:
- users (authentication & profiles)
- categories (service categories)
- offers (service offerings)
- reclamations (user complaints)
- migrations (schema versioning)
```

**Scaling Approach**:
- Master-Slave replication for read scaling
- Sharding for large datasets
- Query optimization and indexing

---

### 3. **Prometheus**

**Role**: Metrics collection and time-series storage

**Configuration**:
- Version: 2.48.0
- Scrape Interval: 15 seconds
- Evaluation Interval: 15 seconds
- Retention: 30 days
- Port: 9090

**Data Sources**:
- Spring Boot Actuator (`/actuator/prometheus`)
- cAdvisor (container metrics)
- Node Exporter (system metrics)

**Key Metrics Collected**:
```
- HTTP Requests:
  * http_requests_total
  * http_request_duration_seconds
  * http_requests_in_progress

- JVM Metrics:
  * jvm_memory_used_bytes
  * jvm_memory_max_bytes
  * jvm_threads_live_threads
  * jvm_gc_pause_seconds

- Application Metrics:
  * spring_cache_gets_total
  * spring_cache_puts_total
  * db_connection_pool_active
```

**Querying**:
- PromQL language for complex queries
- Alerting rules for thresholds
- Integration with Grafana for visualization

---

### 4. **Grafana**

**Role**: Visualization and dashboarding

**Configuration**:
- Version: 10.2.0
- Port: 3000
- Default User: admin
- Datasources: Prometheus, Loki

**Pre-configured Dashboards**:

1. **Prestify Application Dashboard**
   - HTTP request rates
   - JVM memory usage
   - Active threads
   - Application logs
   - Error rates

2. **Database Performance**
   - Query execution time
   - Connection pool usage
   - Slow queries
   - Transaction metrics

3. **Infrastructure Health**
   - CPU utilization
   - Memory usage
   - Disk I/O
   - Network traffic

**Alert Configuration**:
- High memory usage (>85%)
- Application downtime
- Error rate threshold
- Database connection exhaustion

---

### 5. **Loki**

**Role**: Centralized log aggregation

**Configuration**:
- Version: 2.9.3
- Port: 3100
- Retention: 720 hours (30 days)
- Storage: Local filesystem

**Log Sources**:
- Prestify Application (structured JSON)
- MySQL database errors
- Promtail agent logs
- System logs

**Log Processing Pipeline**:
```
Raw Logs
  ├─ JSON Parsing
  ├─ Multiline Support
  ├─ Label Extraction
  └─ Timestamp Normalization
```

**Querying**:
- LogQL language for log queries
- Label-based filtering
- Pattern matching
- Statistics and aggregation

---

### 6. **Promtail**

**Role**: Log collection and forwarding agent

**Configuration**:
- Version: 2.9.3
- Port: 9080

**Collection Methods**:
1. **Docker Integration**
   - Automatic container log discovery
   - Container metadata extraction
   - Dynamic label assignment

2. **File-based Collection**
   - Application log files
   - System logs
   - Database error logs

3. **Processing Stages**:
   - Multiline: Join multi-line log entries
   - Regex: Parse structured logs
   - JSON: Extract fields from JSON logs
   - Labels: Add metadata and context

---

## Data Flow

### 1. **User Request Flow**

```
User Request
     │
     ▼
┌─────────────────────┐
│ Spring Boot App     │
│ (Port 8080)         │
└──────────┬──────────┘
           │
           ├─► Database Query ──► MySQL (Port 3306)
           │                         │
           │                         ▼
           │                    Data Retrieval
           │                         │
           │◄─────────────────────────┘
           │
           ├─► Emit Metrics ──► Prometheus (Port 9090)
           │                      │
           │                      ▼
           │                   Store in TSDB
           │
           ├─► Write Logs ──► Loki (Port 3100)
           │                    │
           │                    ▼
           │                Promtail Collection
           │
           ▼
     HTTP Response
     (JSON/HTML)
```

### 2. **Monitoring Data Flow**

```
Application Metrics
     │
     ├─ HTTP Requests
     ├─ JVM Stats
     ├─ Cache Performance
     └─ Database Metrics
           │
           ▼
    Prometheus Scraper
    (Every 15 seconds)
           │
           ▼
    Time Series Database
    (30-day retention)
           │
           ▼
    Grafana Visualization
           │
           ├─ Real-time Dashboards
           ├─ Historical Analysis
           ├─ Trend Analysis
           └─ Alert Triggers
```

### 3. **Logging Data Flow**

```
Application Logs
     │
     ├─ stdout/stderr (Docker)
     ├─ File logs (/logs/*)
     └─ Structured JSON
           │
           ▼
    Promtail Agent
    (Log Collection)
           │
           ├─ Parse JSON
           ├─ Add Labels
           └─ Normalize Timestamps
           │
           ▼
    Loki API
    (Push API)
           │
           ▼
    Distributed Storage
    (Local filesystem)
           │
           ▼
    LogQL Queries
           │
           ├─ In Grafana
           ├─ In Loki UI
           └─ Alerts/Rules
```

---

## Technology Stack

| Layer | Component | Version | Purpose |
|-------|-----------|---------|---------|
| **Runtime** | Java | 17 | Application runtime |
| **Framework** | Spring Boot | 3.0 | Web framework |
| **Build** | Maven | 3.8.7 | Build automation |
| **Database** | MySQL | 8.0 | Data persistence |
| **Containerization** | Docker | 20.10+ | Container engine |
| **Orchestration** | Docker Compose | 2.0+ | Service orchestration |
| **Metrics** | Prometheus | 2.48.0 | Time-series metrics |
| **Visualization** | Grafana | 10.2.0 | Dashboarding |
| **Logging** | Loki | 2.9.3 | Log aggregation |
| **Log Collection** | Promtail | 2.9.3 | Agent |
| **Deployment** | AWS EC2 | Latest | Infrastructure |
| **CI/CD** | GitLab CI | Latest | Pipeline automation |
| **Monitoring** | Spring Actuator | Built-in | Application metrics |

---

## Deployment Architecture

### Local Development

```
Developer Machine
├── Docker Engine
├── Docker Compose
├── Git Client
├── IDE (IntelliJ/VS Code)
└── MySQL CLI Tools

Services Running Locally:
- Prestify App (localhost:8080)
- MySQL (localhost:3306)
- Prometheus (localhost:9090)
- Grafana (localhost:3000)
- Loki (localhost:3100)
```

### AWS EC2 Production

```
AWS EC2 Instance (t3.medium+)
├── OS: Ubuntu 20.04 LTS
├── Docker Engine
├── Docker Compose
├── CloudWatch Agent (Optional)
└── Auto-scaling Group (Optional)

Security:
- Security Group with port restrictions
- SSH key-based authentication
- IAM roles for AWS service access
- Environment variables for secrets

Volumes:
- EBS volume for application
- Snapshots for backups
- S3 for backup storage

Monitoring:
- CloudWatch integration
- Prometheus metrics
- Grafana dashboards
- Log aggregation to CloudWatch Logs
```

### High Availability Setup (Future)

```
                    ┌──────────────────┐
                    │  Route 53 DNS    │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │  Application     │
                    │  Load Balancer   │
                    └──────┬───────────┘
                           │
        ┌──────────────┬───┼───┬──────────────┐
        │              │       │              │
        ▼              ▼       ▼              ▼
    ┌────────┐     ┌────────┐ ┌────────┐  ┌────────┐
    │ App-1  │     │ App-2  │ │ App-3  │  │ App-N  │
    │EC2     │     │EC2     │ │EC2     │  │EC2     │
    └────────┘     └────────┘ └────────┘  └────────┘
        │              │          │           │
        └──────────────┼──────────┼───────────┘
                       │
                   ┌───▼────────┐
                   │ RDS MySQL  │
                   │(Primary)   │
                   └────────────┘
                        │
        ┌───────────────┴────────────────┐
        │                                │
    ┌───▼──────┐                    ┌────▼────┐
    │ Replica-1│                    │ Replica-2│
    └──────────┘                    └──────────┘
    
    Monitoring Stack (Single Instance):
    - Prometheus
    - Grafana
    - Loki + Promtail
```

---

## Security Architecture

### 1. **Network Security**

- **VPC**: Private/Public subnet segregation
- **Security Groups**: Port-based access control
- **SSH**: Key-based authentication only
- **TLS/SSL**: HTTPS for API endpoints

### 2. **Application Security**

- **Spring Security**: Authentication and authorization
- **JWT Tokens**: Stateless authentication
- **Prepared Statements**: SQL injection prevention
- **Input Validation**: All user inputs validated
- **CORS**: Configured for trusted origins

### 3. **Data Security**

- **Encryption at Rest**: Database encryption
- **Encryption in Transit**: TLS for all communications
- **Secrets Management**: Environment variables, AWS Secrets Manager
- **Database Access**: User-level permissions
- **Backup Encryption**: Encrypted backups

### 4. **Container Security**

- **Non-root User**: App runs as non-root
- **Read-only Filesystem**: Immutable configurations
- **Resource Limits**: Memory and CPU restrictions
- **Image Scanning**: Trivy vulnerability scanning
- **Registry Security**: Private container registry

### 5. **Audit and Compliance**

- **Application Logs**: All actions logged
- **Audit Trail**: User activity tracking
- **Monitoring**: 24/7 metrics and logs
- **Alerts**: Anomaly detection
- **Compliance**: GDPR, PCI-DSS ready architecture

---

## Scalability Strategy

### 1. **Vertical Scaling**

**For Development/Testing**:
```
EC2: t3.small (1 CPU, 2GB RAM)
```

**For Production**:
```
EC2: t3.xlarge (4 CPU, 16GB RAM)
JVM: -Xms2g -Xmx8g
```

### 2. **Horizontal Scaling**

**Application Tier**:
```
Auto Scaling Group:
- Min: 2 instances
- Desired: 2 instances
- Max: 5 instances
- Scale out trigger: CPU > 70% or Memory > 80%
- Scale in trigger: CPU < 30% for 5 minutes
```

**Database Tier**:
```
Primary + Read Replicas:
- Primary: Master (writes)
- Replica 1: Read-only (reports)
- Replica 2: Read-only (backups)
```

### 3. **Caching Strategy**

```
L1 Cache: Spring Cache (Local)
  - User authentication tokens
  - Service categories
  - Offer listings

L2 Cache: Redis (Optional)
  - Distributed cache
  - Session store
  - Rate limiting

Cache Invalidation:
  - TTL: 1 hour default
  - Event-based: On data modification
  - Manual: Admin console
```

### 4. **Database Optimization**

```
Indexing:
- Primary key on all tables
- Foreign key indexes
- Search field indexes
- Date range indexes

Partitioning:
- Time-based: Logs by date
- Range-based: Large tables by ID range

Connection Pooling:
- Min idle: 5 connections
- Max pool: 20 connections
- Connection timeout: 20 seconds
```

### 5. **Performance Optimization**

**Query Optimization**:
```sql
- Use EXPLAIN ANALYZE
- Avoid SELECT *
- Use appropriate JOINs
- Batch operations
```

**API Optimization**:
```
- Response compression (gzip)
- Pagination for large datasets
- Caching headers
- Lazy loading
```

**Container Optimization**:
```
- Multi-stage Docker builds
- Minimal base images
- Layer caching
- Resource limits enforcement
```

---

## Disaster Recovery

### 1. **Backup Strategy**

```
Database:
- Daily automated backups
- Weekly snapshots
- Monthly archival
- Retention: 90 days

Application Code:
- Git repository (GitLab)
- Multiple replicas
- Tag releases

Configuration:
- Infrastructure as Code
- Version controlled
- Documented procedures
```

### 2. **Recovery Time Objectives (RTO)**

```
Critical Service: < 15 minutes
Major Service: < 1 hour
Minor Service: < 4 hours
```

### 3. **Recovery Point Objectives (RPO)**

```
Database: 1 hour (hourly backups)
Logs: 15 minutes (Loki retention)
Metrics: 24 hours (Prometheus retention)
```

### 4. **Failover Procedures**

```
Automated:
- Health check failures trigger auto-failover
- Database replicas promoted automatically
- DNS updated via Route 53

Manual:
- Team-initiated failover steps
- Documented runbook
- Testing procedures

Testing:
- Monthly DR drills
- Quarterly full recovery tests
- Post-incident reviews
```

---

## Conclusion

The Prestify architecture is designed to be:

✅ **Scalable** - Handles growth through horizontal and vertical scaling  
✅ **Reliable** - Built-in redundancy and failover mechanisms  
✅ **Secure** - Multiple layers of security controls  
✅ **Observable** - Comprehensive monitoring and logging  
✅ **Maintainable** - Clear structure and documentation  
✅ **Cost-effective** - Optimized resource utilization  

The architecture follows industry best practices and is suitable for enterprise-grade deployments while remaining flexible for future enhancements and migrations.

---

**Version**: 1.0  
**Last Updated**: 2024  
**Architecture Review**: Quarterly  
**Next Review Date**: Q1 2025
