# Product Service

Service responsible for managing file storage integration with Amazon S3 in the Vendo platform.

The service provides secure file upload and access mechanisms using pre-signed URLs, allowing other services (f.e. Product) to store and retrieve files without direct S3 access.

---

# Tech Stack

* Java 17
* Spring Boot
* AWS SDK v2 (S3 Presigner)
* Docker
* Eureka
* Zipkin
* Lombok
* Maven
* JUnit 5
* Mockito

---

# Architecture

The service follows Hexagonal Architecture (Ports and Adapters).

All AWS/S3 logic is isolated in adapters, while business rules remain independent from AWS SDK.

## Layers

**domain**

Contains the core business logic.

* Models
* Context types (e.g. PRODUCT)
* Exceptions
* Value objects

**application**

Application use cases.

* Generate presigned upload URL
* Validate file metadata
* Generate file keys

**port**

Defines interfaces used to communicate with the outside world.

* Input ports (use cases)
* Output ports (repositories)

**adapter**

External integrations.

adapter.in

* REST Controllers
* Request / Response DTO
* Exception handlers

adapter.out

* Database repositories

---

# Project Structure

```
src
 └── main
     └── java
         └── com.vendo.aws_service
             ├── adapter
             │   └── storage
             │       └── in
             │       └── out
             ├── application
             │   └── storage
             ├── domain
             │   └── storage
             ├── port
             │   └── storage
             └── infrastructure
```

---

# Prerequisites

Before running this service, you need to start required infrastructure services.

## Dependencies

This service depends on:

- **Config Server** – provides externalized configuration
- **Service Registry (Eureka)** – service discovery

---

## 1. Clone and run Config Server

```
git clone https://github.com/vendo-marketplace/config-server
cd config-server
mvn spring-boot:run
```


---

## 2. Clone and run Service Registry

```
git clone https://github.com/vendo-marketplace/registry-service
cd registry-service
mvn spring-boot:run
```


# Running the Service

---

## 3. Run application

Or build and run:

```
mvn clean package
java -jar target/aws-service.jar
```

---

# Environment Variables

| Variable          | Description       | Default   |
|-------------------|-------------------|-----------|
| CONFIG_SERVER_URL | Config server url | 8010      |

---

# API Documentation

Swagger UI:

```
http://localhost:9010/swagger-ui/index.html
```

OpenAPI specification:

```
http://localhost:9010/v3/api-docs
```

---

# Running Tests

Run all tests

```
mvn test
```

Run integration tests

```
mvn verify
```

---

# Code Style

The project follows standard **Java code conventions**.

Key principles:

* Clean Architecture
* SOLID principles
* Immutable DTOs
* Constructor injection
* Clear separation between layers

---

# Contributing

1. Create feature branch
2. Write tests
3. Ensure tests pass
4. Create pull request
