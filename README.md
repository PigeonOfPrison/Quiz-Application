# Quiz Application - Microservices Architecture

A comprehensive quiz platform built using Spring Boot microservices architecture with service discovery, API gateway, and distributed system patterns.

## 🏗️ Architecture Overview

This project implements a microservices-based quiz application with the following key components:

### Core Services
- **Service Registry** (Port 8761) - Netflix Eureka Server for service discovery
- **API Gateway** (Port 8765) - Spring Cloud Gateway with routing, resilience patterns, and authentication
- **Auth Service** (Port 7999) - Authentication and authorization with JWT and OAuth2
- **Quiz Service** (Port 8090) - Quiz management and operations
- **Question Service** - Question bank management
- **API Content Service** (Port 8085) - Content delivery with Redis caching
- **Notification Service** (Port 7900) - Email notifications via Kafka
- **Analytics Service** - Analytics and reporting
- **Logging Service** - Centralized logging
- **User Data Service** - User profile and data management

## 🚀 Technology Stack

### Core Technologies
- **Java 21** - Primary programming language
- **Spring Boot 3.5.x** - Application framework
- **Spring Cloud 2025.0.0** - Microservices infrastructure
- **Maven** - Build and dependency management

### Service Discovery & Communication
- **Netflix Eureka** - Service registry (legacy, commented in most services)
- **Consul** - Current service discovery solution
- **OpenFeign** - Declarative REST client for inter-service communication

### API Gateway & Resilience
- **Spring Cloud Gateway** - API gateway with WebFlux
- **Resilience4j** - Circuit breaker, retry, time limiter, and bulkhead patterns

### Data & Caching
- **PostgreSQL** - Primary database for Quiz, Question, and Auth services
- **Redis** - Caching layer for API content service
- **Spring Data JPA** - Data persistence layer

### Messaging & Events
- **Apache Kafka** - Event-driven messaging for notifications

### Security
- **Spring Security** - Authentication and authorization
- **JWT** - Token-based authentication
- **OAuth2** - Social login (Google & GitHub)

### Monitoring & Observability
- **Spring Actuator** - Application metrics and health checks
- **Prometheus** - Metrics collection
- **Consul Health Checks** - Service health monitoring

## 📋 Prerequisites

Before running this application, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.8+**
- **Docker** (for running infrastructure services)
- **PostgreSQL** (or use Docker)
- **Redis** (or use Docker)
- **Apache Kafka** (or use Docker)
- **Consul** (or use Docker)

## 🔧 Configuration

### Environment Variables

Create a `.env` file or set the following environment variables:

```bash
# PostgreSQL
POSTGRES_USERNAME=your_username
POSTGRES_PASSWORD=your_password

# JWT Configuration
JWT_SECRET_KEY=your_jwt_secret_key_base64_encoded
JWT_EXPIRATION=3600000

# OAuth2 - Google
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# OAuth2 - GitHub
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret
```

### Database Setup

#### Step 1: Create Databases

Create the following PostgreSQL databases:

```sql
CREATE DATABASE securitydb;  -- For auth-service
CREATE DATABASE quizdb;      -- For quiz-service
CREATE DATABASE questiondb;  -- For question-service
```

Default PostgreSQL connection:
- Host: `localhost`
- Port: `6543`
- Username: Set via `POSTGRES_USERNAME` env variable
- Password: Set via `POSTGRES_PASSWORD` env variable

#### Step 2: Run Schema Scripts

After creating the databases, run the provided SQL schema files to create the necessary tables and types:

**For Quiz Service:**
```bash
psql -U postgres -h localhost -p 6543 -d quizdb -f quiz-service/schema.sql
```

**For Question Service:**
```bash
psql -U postgres -h localhost -p 6543 -d questiondb -f question-service/schema.sql
```

The schema files include:
- **`quiz-service/schema.sql`**: Creates `quiz` and `quiz_question_ids` tables with proper indexes and constraints
- **`question-service/schema.sql`**: Creates the `difficulty` enum type, `question` table, and the required VARCHAR to difficulty CAST for JPA compatibility

**Note**: Both schema files include optional sample data (commented out). Uncomment the INSERT statements if you want to populate the database with test data.

## 🐳 Running Infrastructure Services with Docker

### 1. Start Consul (Service Discovery)

```bash
docker run -d --name=consul \
  -p 8500:8500 \
  -p 8600:8600/udp \
  -v consul-data:/consul/data \
  consul:latest agent -server -bootstrap-expect=1 -ui -client=0.0.0.0
```

Access Consul UI: http://localhost:8500

### 2. Start Kafka (Message Broker)

```bash
docker run -d --name kafka-vol -p 9092:9092 \
  -v kafka-data:/var/lib/kafka/data \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:29093 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_LISTENERS=PLAINTEXT://localhost:9092,CONTROLLER://localhost:29093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_LISTENER_SECURITY_PROTOCOL_MAP=CONTROLLER:PLAINTEXT,PLAINTEXT:PLAINTEXT \
  -e KAFKA_INTER_BROKER_LISTENER_NAME=PLAINTEXT \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  -e KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:latest
```

### 3. Start Redis (Caching)

```bash
docker run -d --name redis -p 6379:6379 redis:latest
```

### 4. Start PostgreSQL (Database)

```bash
docker run -d --name postgres \
  -p 6543:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=your_password \
  -v postgres-data:/var/lib/postgresql/data \
  postgres:latest
```

## 🏃 Running the Application

### Recommended Startup Order

1. **Service Registry** (Optional - if using Eureka instead of Consul)
```bash
cd service-registry
./mvnw spring-boot:run
```

2. **API Gateway**
```bash
cd api-gateway
./mvnw spring-boot:run
```

3. **Auth Service**
```bash
cd auth-service
./mvnw spring-boot:run
```

4. **Core Business Services** (can be started in parallel)
```bash
# Terminal 1
cd quiz-service
./mvnw spring-boot:run

# Terminal 2
cd question-service
./mvnw spring-boot:run

# Terminal 3
cd api-content-service
./mvnw spring-boot:run

# Terminal 4
cd notification-service
./mvnw spring-boot:run
```

5. **Supporting Services**
```bash
# Terminal 5
cd analytics-service
./mvnw spring-boot:run

# Terminal 6
cd logging-service
./mvnw spring-boot:run

# Terminal 7
cd user-data
./mvnw spring-boot:run
```

### Build All Services

To build all services at once:

```bash
# Build without running tests
for dir in */; do
  if [ -f "$dir/pom.xml" ]; then
    echo "Building $dir"
    cd "$dir"
    ./mvnw clean install -DskipTests
    cd ..
  fi
done
```

## 📡 Service Endpoints

| Service | Port | Description | Health Check |
|---------|------|-------------|--------------|
| Service Registry | 8761 | Eureka Dashboard | http://localhost:8761 |
| API Gateway | 8765 | Main entry point | http://localhost:8765/actuator/health |
| Auth Service | 7999 | Authentication | http://localhost:7999/actuator/health |
| Quiz Service | 8090 | Quiz operations | http://localhost:8090/actuator/health |
| Question Service | - | Question management | - |
| API Content Service | 8085 | Content delivery | http://localhost:8085/actuator/health |
| Notification Service | 7900 | Email notifications | - |
| Consul UI | 8500 | Service discovery | http://localhost:8500 |

## 🔒 Authentication

### JWT Authentication
The API Gateway validates JWT tokens for secured endpoints.

**Token Header:**
```
Authorization: Bearer <your_jwt_token>
```

### OAuth2 Social Login
Supported providers:
- **Google**: Redirect to `http://localhost:7999/auth/oauth2/google/callback`
- **GitHub**: Redirect to `http://localhost:7999/auth/oauth2/github/callback`

## 🛡️ Resilience Patterns

The API Gateway implements the following resilience patterns using Resilience4j:

### Circuit Breaker
- Sliding window size: 10 calls
- Minimum calls: 5
- Failure rate threshold: 50%
- Wait duration in open state: 5s

### Retry
- Max attempts: 3
- Wait duration: 2s

### Time Limiter
- Timeout duration: 2s

### Bulkhead
- Max concurrent calls: 5

## 📊 Monitoring

### Health Checks
All services expose health endpoints via Spring Actuator:
```
http://localhost:<service-port>/actuator/health
```

### Prometheus Metrics
The API Gateway exposes Prometheus metrics:
```
http://localhost:8765/actuator/prometheus
```

### Consul Monitoring
Monitor service health and registration in Consul UI:
```
http://localhost:8500
```

## 🔄 CORS Configuration

The API Gateway is configured to accept requests from:
- `http://localhost:5173` (Vite development server)
- `http://localhost:3000` (React development server)

Allowed methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

## 🗄️ Database Migrations

Each service uses Hibernate with DDL mode set to `validate`. Make sure to:
1. Create the database schemas manually
2. Run migration scripts (if available) before starting services
3. Ensure database credentials are correctly configured

## 📧 Email Configuration

The Notification Service uses Gmail SMTP:
- Host: `smtp.gmail.com`
- Port: `587` (TLS)
- Authentication: Required
- Update credentials in `notification-service/src/main/resources/application.properties`

## 🧪 Testing

Run tests for individual services:

```bash
cd <service-directory>
./mvnw test
```

## 📦 Building for Production

### Create JAR files

```bash
cd <service-directory>
./mvnw clean package
```

The JAR file will be created in the `target/` directory.

### Running JAR files

```bash
java -jar target/<service-name>-0.0.1-SNAPSHOT.jar
```

## 🐛 Troubleshooting

### Service not registering with Consul
- Ensure Consul is running: `docker ps | grep consul`
- Check service health endpoint is accessible
- Verify `spring.cloud.consul.discovery.health-check-path=/actuator/health` is configured

### Database connection errors
- Verify PostgreSQL is running and accessible
- Check environment variables are set correctly
- Ensure databases are created

### Kafka connection issues
- Verify Kafka container is running: `docker ps | grep kafka`
- Check Kafka is listening on port 9092: `netstat -an | grep 9092`

### OAuth2 authentication failures
- Verify OAuth2 credentials in environment variables
- Check redirect URIs match the configuration in Google/GitHub console
- Ensure `http://localhost:7999` is in allowed origins

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is part of a learning exercise for microservices architecture.

## 👤 Author

**PigeonOfPrison**
- GitHub: [@PigeonOfPrison](https://github.com/PigeonOfPrison)

## 🙏 Acknowledgments

- Spring Cloud documentation and community
- Netflix OSS for Eureka
- HashiCorp for Consul
- All open-source contributors

---

**Note**: This is a development setup. For production deployment, consider:
- Using environment-specific configuration files
- Implementing centralized configuration management (Spring Cloud Config)
- Setting up proper logging aggregation (ELK stack)
- Implementing distributed tracing (Zipkin/Jaeger)
- Using container orchestration (Kubernetes/Docker Swarm)
- Setting up CI/CD pipelines
- Implementing proper secret management (Vault)
