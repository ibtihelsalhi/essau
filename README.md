# Prestify Platform - Service Management System

[![Build Status](https://gitlab.com/dhibimouna2-spec/prestify/badges/main/pipeline.svg)](https://gitlab.com/dhibimouna2-spec/prestify/-/pipelines)
[![Coverage Status](https://gitlab.com/dhibimouna2-spec/prestify/badges/main/coverage.svg)](https://gitlab.com/dhibimouna2-spec/prestify/-/commits/main)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Prestify is a comprehensive **service management platform** designed to streamline service provision, user management, and claim handling. Built with **Spring Boot 3.0**, **Docker**, and **AWS**, with integrated monitoring via **Prometheus** and **Grafana**.

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Team](#team)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Local Development](#local-development)
  - [Docker Setup](#docker-setup)
- [CI/CD Pipeline](#cicd-pipeline)
- [Deployment](#deployment)
- [Monitoring](#monitoring)
- [Testing](#testing)
- [Documentation](#documentation)
- [Contributing](#contributing)

---

## ✨ Features

### Core Functionality
- **User Management**: Registration, authentication, profile management
- **Service Offers**: Create, read, update, delete service offerings
- **Categories**: Organize services by categories
- **Reclamations/Claims**: Handle user complaints and requests
- **Role-Based Access Control**: Admin, Provider, Customer roles

### Technical Features
- ✅ RESTful API with Spring Boot
- ✅ JPA/Hibernate ORM
- ✅ MySQL relational database
- ✅ JWT authentication (optional)
- ✅ Comprehensive logging (Loki aggregation)
- ✅ Metrics and monitoring (Prometheus + Grafana)
- ✅ Health checks and actuator endpoints
- ✅ Docker containerization with multi-stage builds
- ✅ Automated CI/CD pipeline (GitLab)
- ✅ Security scanning (SAST, container, secrets)

---

## 🛠 Tech Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| **Runtime** | Java 17 (Temurin) | JVM for Spring Boot |
| **Framework** | Spring Boot 3.0 | REST API & Business Logic |
| **Database** | MySQL 8.0 | Data persistence |
| **Containerization** | Docker 24+ | Packaging & deployment |
| **Orchestration** | Docker Compose | Multi-container management |
| **Monitoring** | Prometheus 2.48 | Metrics collection |
| **Visualization** | Grafana 10.2 | Dashboard & alerts |
| **Logging** | Loki 2.9.3 | Log aggregation |
| **CI/CD** | GitLab CI/CD | Automation pipeline |
| **Cloud** | AWS EC2 | Production hosting |

---

## 👥 Team

| Role | Member | Responsibilities |
|------|--------|------------------|
| Product Owner | [Your Name] | Project vision, requirements, prioritization |
| DevOps Lead | [Your Name] | Infrastructure, CI/CD, deployment |
| Backend Developer | [Your Name] | API development, business logic |
| QA / Tester | [Your Name] | Testing, quality assurance |

---

## 🏗 Architecture

### System Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│                  CLIENT LAYER                   │
│          (Web Browser / REST Client)            │
└────────────────────┬────────────────────────────┘
                     │ HTTP/HTTPS
                     ▼
┌─────────────────────────────────────────────────┐
│              AWS EC2 (Docker Host)              │
│  ┌───────────────────────────────────────────┐  │
│  │   DOCKER NETWORK (prestify_network)       │  │
│  │                                           │  │
│  │  ┌──────────────┐  ┌──────────────────┐  │  │
│  │  │ Spring Boot  │◄─┤ MySQL Database   │  │  │
│  │  │  (Port 8080) │  │ (Port 3306)      │  │  │
│  │  └──────────────┘  └──────────────────┘  │  │
│  │        │                                  │  │
│  │        ├─► Prometheus ◄─── Grafana      │  │
│  │        └─► Loki ◄─────── Promtail      │  │
│  │                                           │  │
│  └───────────────────────────────────────────┘  │
│                                                 │
│  Volumes: mysql_data, prometheus_data,         │
│           loki_data, grafana_data              │
└─────────────────────────────────────────────────┘
```

For detailed architecture, see [ARCHITECTURE.md](./ARCHITECTURE.md).

---

## 🚀 Getting Started

### Prerequisites

**System Requirements:**
- OS: Windows 10+, macOS 10.15+, or Linux (Ubuntu 18.04+)
- RAM: Minimum 4GB (8GB recommended)
- Disk: 10GB free space
- Docker: 20.10+
- Docker Compose: 2.0+
- Git: Latest version
- Java 17+ (for local Maven builds)
- Maven 3.8.7+

**Install Required Tools:**

```bash
# Verify installations
docker --version
docker-compose --version
git --version
java -version
mvn --version
```

### Local Development

**1. Clone Repository**

```bash
git clone https://gitlab.com/your-org/prestify.git
cd prestify
```

**2. Setup Environment Variables**

```bash
# Copy example environment file
cp .env.example .env

# Edit .env with your values
nano .env  # or use your preferred editor
```

**Key environment variables to configure:**
```env
MYSQL_PASSWORD=your_secure_password_here
MYSQL_ROOT_PASSWORD=your_root_password_here
GRAFANA_PASSWORD=your_grafana_password_here
```

**3. Build Application Locally (Optional)**

```bash
# Navigate to application source
cd Prestify_Plateforme_de_gestion_des_services-main

# Compile
mvn clean compile

# Run unit tests
mvn clean test

# Build JAR package
mvn clean package

# Return to root
cd ..
```

### Docker Setup

**1. Development Environment**

```bash
# Build all services
docker-compose build

# Start services
docker-compose -f docker-compose.yml -f docker-compose.dev.yml up -d

# Verify services are running
docker-compose ps

# View logs
docker-compose logs -f prestify_app
```

**2. Verify Deployment**

```bash
# Application health
curl http://localhost:8080/actuator/health

# Database connection
docker exec prestify_mysql mysql -u prestify_user -p -e "SELECT 1;"

# Grafana access
# URL: http://localhost:3000
# Username: admin
# Password: admin123 (or your GRAFANA_PASSWORD)
```

**3. Access Services**

| Service | URL | Default Credentials |
|---------|-----|-------------------|
| **Application API** | http://localhost:8080 | - |
| **Grafana** | http://localhost:3000 | admin / admin123 |
| **Prometheus** | http://localhost:9090 | - |
| **phpMyAdmin** | http://localhost:8081 | prestify_user / prestify_pass |

**4. Stop Services**

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes (data loss)
docker-compose down -v
```

---

## 🔄 CI/CD Pipeline

The GitLab CI/CD pipeline automates build, test, security, and deployment stages.

**Pipeline Stages:**

1. **Build** (`.pre`)
   - YAML validation
   - Maven compilation
   - Docker image build

2. **Test**
   - Unit tests (JUnit + Jacoco coverage)
   - Integration tests
   - Code quality analysis (PMD, SpotBugs, Checkstyle)
   - Dependency checking

3. **Security**
   - SAST analysis (SpotBugs + Findsecbugs)
   - Docker image scanning (Trivy)
   - Secrets detection (Trufflehog)
   - Filesystem vulnerability scan

4. **Deploy**
   - Staging deployment (manual trigger)
   - Production deployment (manual trigger)
   - Rollback capability

**Pipeline Files:**
- `.gitlab-ci.yml` - Main pipeline configuration
- `ci/variables.yml` - Global variables
- `ci/build.yml` - Build stage jobs
- `ci/test.yml` - Test stage jobs
- `ci/security.yml` - Security stage jobs
- `ci/deploy.yml` - Deployment stage jobs

**Trigger Pipeline:**

```bash
# Push changes (automatic pipeline trigger)
git push origin feature-branch

# Create merge request (automatic pipeline trigger)
# Pipeline must pass before merging to main/develop
```

---

## 📦 Deployment

### Local Docker Deployment

```bash
# Production-like setup
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Verify health
curl -f http://localhost:8080/actuator/health/liveness
```

### AWS EC2 Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for complete AWS deployment guide including:
- EC2 instance setup
- Security group configuration
- SSH key management
- Auto-scaling setup
- Load balancer configuration

**Quick Start:**

```bash
# SSH into EC2
ssh -i your-key.pem ec2-user@<EC2_PUBLIC_IP>

# Deploy application
cd /opt/prestify
docker-compose pull
docker-compose up -d

# View logs
docker-compose logs -f prestify_app
```

---

## 📊 Monitoring

### Grafana Dashboards

Access Grafana at http://localhost:3000

**Available Dashboards:**
1. **Prestify Application Dashboard** - HTTP requests, JVM metrics, logs
2. **Database Performance** - Query times, connections, transactions
3. **Infrastructure Health** - CPU, memory, disk, network

### Prometheus Metrics

Access at http://localhost:9090

**Key Metrics:**
```
- http_requests_total
- http_request_duration_seconds
- jvm_memory_used_bytes
- jvm_threads_live_threads
- spring_cache_gets_total
- db_connection_pool_active
```

### Application Logs

Access via Grafana Loki (Explore → Loki datasource)

**Query Examples:**
```
{job="prestify"}
{level="ERROR"}
{container_name="prestify_app"}
```

---

## ✅ Testing

### Run Unit Tests

```bash
cd Prestify_Plateforme_de_gestion_des_services-main
mvn clean test
```

### View Test Reports

```bash
# Test results
cat target/surefire-reports/TEST-*.txt

# Code coverage report
open target/site/jacoco/index.html
```

### Code Quality Analysis

```bash
# PMD (code style)
mvn pmd:check

# SpotBugs (bug detection)
mvn spotbugs:check

# Checkstyle (formatting)
mvn checkstyle:check

# Dependency vulnerability check
mvn dependency-check:aggregate
```

---

## 📚 Documentation

- [ARCHITECTURE.md](./ARCHITECTURE.md) - Technical architecture details
- [DEPLOYMENT.md](./DEPLOYMENT.md) - AWS deployment guide
- [USER_STORIES.md](./USER_STORIES.md) - Project requirements and user stories
- [CI/CD Pipeline](./ci/) - Pipeline configuration files

---

## 🤝 Contributing

### Git Workflow

**Branch Strategy:**
- `main` - Production code (protected)
- `develop` - Integration branch (protected)
- `feature/*` - New features
- `hotfix/*` - Urgent fixes

**Commit Convention:**
```
feat(scope): add new feature
fix(api): resolve bug in endpoint
docs(readme): update documentation
ci(pipeline): add security stage
test(unit): add test coverage
```

### Pull Request Process

1. Create feature branch from `develop`
2. Make changes and commit frequently
3. Push to GitLab
4. Create Merge Request with description
5. Pass CI/CD pipeline
6. Request code review (minimum 1 reviewer)
7. Merge to target branch

### Code Quality Standards

- **Test Coverage:** Minimum 60%
- **Build:** Must pass CI/CD pipeline
- **Code Review:** At least 1 approval required
- **Documentation:** Update docs for new features

---

## 📝 License

This project is licensed under the MIT License - see [LICENSE](./LICENSE) file for details.

---

## 📞 Support

For issues, questions, or suggestions:
- Create a GitLab issue
- Contact the Product Owner
- Check existing documentation

---

**Last Updated:** December 2024  
**Version:** 1.0.0  
**Status:** Active Development
