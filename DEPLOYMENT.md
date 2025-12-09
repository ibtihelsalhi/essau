# Deployment Guide - Prestify Platform

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Local Development Setup](#local-development-setup)
3. [Docker Deployment](#docker-deployment)
4. [AWS EC2 Deployment](#aws-ec2-deployment)
5. [Post-Deployment Verification](#post-deployment-verification)
6. [Monitoring and Logging](#monitoring-and-logging)
7. [Troubleshooting](#troubleshooting)

---

## Prerequisites

### System Requirements
- **OS**: Linux (Ubuntu 20.04+) or macOS
- **RAM**: Minimum 4GB (8GB recommended)
- **Storage**: 20GB available disk space
- **Docker**: 20.10+
- **Docker Compose**: 2.0+
- **Git**: Latest version

### AWS Requirements
- AWS Account with EC2 access
- EC2 instance (t3.medium or larger)
- Security group with ports open: 22 (SSH), 8080 (App), 3306 (MySQL), 3000 (Grafana), 9090 (Prometheus)
- SSH key pair for instance access
- IAM user with EC2 and CloudWatch permissions

### Required Tools for AWS
```bash
# Install AWS CLI
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install

# Configure AWS credentials
aws configure
```

---

## Local Development Setup

### 1. Clone Repository
```bash
git clone https://gitlab.com/your-team/prestify.git
cd prestify
```

### 2. Setup Environment Variables
```bash
# Create .env file from template
cp .env.example .env

# Edit .env with your configuration
nano .env
```

**Key variables to update:**
```env
MYSQL_PASSWORD=your_secure_password
MYSQL_ROOT_PASSWORD=your_root_password
GRAFANA_PASSWORD=your_grafana_password
JWT_SECRET=your_jwt_secret_key
```

### 3. Verify Docker Installation
```bash
docker --version
docker-compose --version
```

### 4. Build Application
```bash
# Test local build
./mvnw clean install

# Or use Maven directly
cd Prestify_Plateforme_de_gestion_des_services-main
mvn clean package
```

---

## Docker Deployment

### 1. Local Docker Deployment (Testing)

```bash
# Navigate to project root
cd /path/to/prestify

# Build all services
docker-compose build

# Start services
docker-compose up -d

# Verify all services are running
docker-compose ps
```

**Expected output:**
```
NAME                    STATUS
prestify_mysql          Up (healthy)
prestify_app            Up (healthy)
prestify_prometheus     Up
prestify_grafana        Up
prestify_loki           Up
prestify_promtail       Up
```

### 2. Verify Services

**Application Health:**
```bash
curl http://localhost:8080/actuator/health
```

**MySQL Connection:**
```bash
docker exec prestify_mysql mysql -u prestify_user -p -e "SELECT 1;"
```

**Grafana Access:**
- URL: `http://localhost:3000`
- Username: `admin`
- Password: From `.env` (GRAFANA_PASSWORD)

**Prometheus Access:**
- URL: `http://localhost:9090`
- Check targets: `http://localhost:9090/targets`

### 3. View Logs

```bash
# Application logs
docker-compose logs -f prestify_app

# MySQL logs
docker-compose logs -f mysql

# All logs
docker-compose logs -f
```

### 4. Database Initialization

```bash
# Connect to MySQL
docker exec -it prestify_mysql mysql -u root -p

# Create tables (if needed)
USE prestify_db;
SHOW TABLES;
```

---

## AWS EC2 Deployment

### 1. Launch EC2 Instance

```bash
# Using AWS Console or CLI
aws ec2 run-instances \
  --image-id ami-0c94855ba95c574c8 \
  --instance-type t3.medium \
  --key-name your-key-pair \
  --security-groups prestify-sg \
  --region eu-west-1
```

### 2. Configure Security Group

```bash
# Allow SSH
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp --port 22 --cidr 0.0.0.0/0

# Allow HTTP/HTTPS
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp --port 8080 --cidr 0.0.0.0/0

# Allow MySQL
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp --port 3306 --cidr 10.0.0.0/8

# Allow Grafana
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp --port 3000 --cidr 0.0.0.0/0

# Allow Prometheus
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp --port 9090 --cidr 10.0.0.0/8
```

### 3. Connect to Instance

```bash
# SSH into instance
ssh -i your-key.pem ec2-user@<EC2_PUBLIC_IP>

# Update system
sudo yum update -y

# Install Docker
sudo yum install docker -y
sudo systemctl start docker
sudo usermod -a -G docker ec2-user

# Install Docker Compose
sudo curl -L \
  "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 4. Deploy Application

```bash
# Create application directory
mkdir -p /opt/prestify
cd /opt/prestify

# Clone repository
git clone https://gitlab.com/your-team/prestify.git .

# Setup environment
cp .env.example .env
nano .env  # Update with production values

# Start services
docker-compose up -d

# Verify deployment
docker-compose ps
```

### 5. Setup Auto-Scaling (Optional)

```bash
# Create launch template
aws ec2 create-launch-template \
  --launch-template-name prestify-template \
  --version-description "Prestify v1" \
  --launch-template-data file://launch-template.json

# Create Auto Scaling Group
aws autoscaling create-auto-scaling-group \
  --auto-scaling-group-name prestify-asg \
  --launch-template LaunchTemplateName=prestify-template \
  --min-size 1 --max-size 3 --desired-capacity 2
```

### 6. Setup Load Balancer (Optional)

```bash
# Create Application Load Balancer
aws elbv2 create-load-balancer \
  --name prestify-alb \
  --subnets subnet-xxxxx subnet-yyyyy \
  --security-groups sg-xxxxx \
  --scheme internet-facing

# Create target group
aws elbv2 create-target-group \
  --name prestify-targets \
  --protocol HTTP \
  --port 8080 \
  --vpc-id vpc-xxxxx
```

---

## Post-Deployment Verification

### 1. Health Checks

```bash
# Application health
curl -v http://<EC2_PUBLIC_IP>:8080/actuator/health

# Expected response:
# {"status":"UP","components":{"db":{"status":"UP"},...}}
```

### 2. Database Verification

```bash
# SSH into instance
ssh -i key.pem ec2-user@<EC2_PUBLIC_IP>

# Connect to MySQL
docker exec prestify_mysql mysql \
  -u prestify_user \
  -p${MYSQL_PASSWORD} \
  -e "SELECT COUNT(*) as table_count FROM information_schema.tables WHERE table_schema='prestify_db';"
```

### 3. Monitoring Stack Verification

```bash
# Prometheus metrics
curl http://<EC2_PUBLIC_IP>:9090/api/v1/targets

# Grafana login
# URL: http://<EC2_PUBLIC_IP>:3000
# Verify datasources are connected
```

---

## Monitoring and Logging

### 1. Access Grafana Dashboard

```
URL: http://<EC2_PUBLIC_IP>:3000
Username: admin
Password: <GRAFANA_PASSWORD from .env>
```

**Available Dashboards:**
- Prestify Application Dashboard
- JVM Metrics
- Application Performance
- Database Metrics
- Container Health

### 2. View Application Logs (Loki)

```bash
# In Grafana, go to Explore → Select Loki data source
# Query examples:
{job="prestify"}
{level="ERROR"}
{container_name="prestify_app"}
```

### 3. Prometheus Metrics

```
URL: http://<EC2_PUBLIC_IP>:9090
```

**Key Metrics:**
```
jvm_memory_used_bytes - JVM Memory Usage
jvm_threads_live_threads - Active Threads
http_requests_total - Total HTTP Requests
spring_cache_gets_total - Cache Performance
```

### 4. Alert Configuration

Edit `docker-compose.yml` to add AlertManager:

```yaml
alertmanager:
  image: prom/alertmanager:latest
  ports:
    - "9093:9093"
  volumes:
    - ./config/alertmanager.yml:/etc/alertmanager/alertmanager.yml
```

---

## Scaling Considerations

### 1. Database Replication (Optional)

```bash
# Setup MySQL replication for high availability
# Master-Slave configuration in docker-compose
```

### 2. Caching Layer (Optional)

```yaml
# Add Redis to docker-compose.yml
redis:
  image: redis:7-alpine
  ports:
    - "6379:6379"
```

### 3. CI/CD Integration

GitLab CI/CD automatically deploys:
- **Develop branch** → Staging environment
- **Main branch** → Production environment

Manual deployment from GitLab:
1. Go to **Deployments** → **Environments**
2. Click **Deploy** on staging/production
3. Monitor deployment in **CI/CD** → **Pipelines**

---

## Troubleshooting

### Issue: Docker Compose Services Not Starting

```bash
# Check logs
docker-compose logs

# Reset all services
docker-compose down -v
docker-compose up -d

# Rebuild images
docker-compose build --no-cache
docker-compose up -d
```

### Issue: Database Connection Error

```bash
# Verify MySQL is running
docker ps | grep mysql

# Check MySQL logs
docker logs prestify_mysql

# Verify credentials in .env
docker exec prestify_mysql mysql -u root -p -e "SHOW DATABASES;"
```

### Issue: Application Not Starting

```bash
# Check app logs
docker logs prestify_app

# Verify heap size
docker exec prestify_app java -XX:+PrintFlagsFinal -version | grep HeapSize

# Increase JVM memory if needed
export JAVA_OPTS="-Xms1g -Xmx2g"
docker-compose restart prestify_app
```

### Issue: Grafana Datasource Connection Failed

```bash
# Verify Prometheus is accessible from Grafana container
docker exec prestify_grafana curl http://prometheus:9090

# Check network
docker network ls
docker network inspect prestify_network
```

### Issue: High Memory Usage

```bash
# Monitor resource usage
docker stats

# Reduce JVM heap size if needed
# Edit docker-compose.yml:
environment:
  JAVA_OPTS: "-Xms256m -Xmx512m"

# Restart service
docker-compose restart prestify_app
```

### Issue: Slow Application Performance

```bash
# Check database performance
docker exec prestify_mysql mysql -u root -p -e "SHOW PROCESSLIST;"

# Monitor JVM
curl http://<EC2_IP>:8080/actuator/metrics/jvm.memory.usage

# Check Prometheus queries
# http://<EC2_IP>:9090/graph
```

---

## Backup and Recovery

### Database Backup

```bash
# Manual backup
docker exec prestify_mysql mysqldump \
  -u prestify_user \
  -p${MYSQL_PASSWORD} \
  prestify_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Automated daily backup
0 2 * * * docker exec prestify_mysql mysqldump \
  -u prestify_user \
  -p${MYSQL_PASSWORD} \
  prestify_db > /backups/prestify_$(date +\%Y\%m\%d).sql
```

### Database Restore

```bash
# Restore from backup
docker exec -i prestify_mysql mysql \
  -u prestify_user \
  -p${MYSQL_PASSWORD} \
  prestify_db < backup_20240101_020000.sql
```

---

## Production Checklist

- [ ] Environment variables configured and secured
- [ ] SSL/TLS certificates installed (if HTTPS)
- [ ] Database backups scheduled
- [ ] Monitoring alerts configured
- [ ] Log retention configured
- [ ] Auto-scaling policies set
- [ ] Load balancer health checks passing
- [ ] Security group rules reviewed
- [ ] SSH access restricted to known IPs
- [ ] GitLab CI/CD variables configured
- [ ] Application tested end-to-end
- [ ] Documentation updated
- [ ] Team trained on deployment process
- [ ] Runbooks for common issues created
- [ ] Disaster recovery plan documented

---

## Support and Documentation

- **GitLab Repository**: https://gitlab.com/your-team/prestify
- **Monitoring**: http://<EC2_PUBLIC_IP>:3000 (Grafana)
- **API Documentation**: http://<EC2_PUBLIC_IP>:8080/swagger-ui.html
- **Architecture Guide**: See `ARCHITECTURE.md`
- **Issue Tracking**: GitLab Issues

---

**Last Updated**: 2024
**Version**: 1.0
