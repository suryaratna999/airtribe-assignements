# Chronos: Distributed Job Scheduler System
**Author:** Dasari Surya Ratnakara Naidu
**Project:** Backend Engineering Capstone - Airtribe

Chronos is a distributed job scheduling system built with Spring Boot 3.3, Java 21, and PostgreSQL (Supabase). It handles high-concurrency job processing by decoupling the API interface from execution workers.

## 🏗 System Architecture
The system consists of two independent services orchestrated via Docker Compose:
- **Producer API**: A RESTful service for submitting, viewing, and managing jobs.
- **Consumer Worker**: A background service that polls the database and executes jobs.
- **Database**: PostgreSQL hosted on Supabase, acting as the centralized job queue.

## 🚀 Setup & Launch

### 1. Configure Environment
Create a file named .env in the root directory and add your secrets:
DB_PASSWORD=
API_KEY=

### 2. Build JARs
Run this command from the root folder to compile both the Producer and Consumer modules. This generates the necessary files in the target folders:
mvn clean package -DskipTests

### 3. Run Services
Launch the entire system using Docker Compose. This builds the images and starts both services:
docker-compose up --build

## 🛠 Key Features & Design Decisions
- **Distributed Locking**: Uses FOR UPDATE SKIP LOCKED logic to prevent multiple consumer instances from processing the same job simultaneously.
- **Horizontal Scalability**: The worker service can be scaled independently using docker-compose up --scale consumer=3.
- **Security**: REST endpoints are secured with a custom ApiKeyFilter requiring a specific X-API-KEY header.
- **Robustness**: Implements automatic retries and failure logging for non-performing jobs.