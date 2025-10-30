# 🤖 Sarcastic Tweet Bot - Automated News Commentary System

An intelligent microservices-based application that automatically fetches trending news, generates witty sarcastic tweets using AI, and posts them to Twitter - all completely automated!

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.3-blue.svg)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

---

## 📋 Table of Contents

- [Overview](#Overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technologies](#Technologies)
- [Prerequisites](#Prerequisites)
- [Installation](#Installation)
- [Configuration](#Configuration)
- [Running the Application](#Running-the-application)
- [API Documentation](#Api-documentation)
- 
---

## 🎯 Overview

The Sarcastic Tweet Bot is a fully automated system that:
1. **Fetches** trending news from NewsData.io API
2. **Generates** creative, sarcastic tweets using Google's Gemini AI
3. **Posts** tweets to Twitter automatically
4. **Notifies** you of successful posts via email/console

Perfect for creating an engaging, automated social media presence with AI-generated commentary!

---

## ✨ Features

### 🔄 Automated Workflow
- Scheduled news fetching (every 2 hours, configurable)
- Automatic tweet generation and posting
- Self-healing retry logic for failed operations
- Duplicate prevention across all stages

### 🌐 Flexible News Fetching
- Support for 70+ countries
- 19 news categories
- 14+ languages
- Keyword search capability
- Customizable filters (domain priority, full content, images)

### 🤖 AI-Powered Content Generation
- Google Gemini 1.5 Flash integration
- Context-aware sarcastic commentary
- 280-character Twitter optimization
- Configurable tone and style

### 🐦 Twitter Integration
- OAuth 1.0a authentication
- Full Read & Write permissions
- Rate limit handling
- Mock mode for testing


### 🏗️ Microservices Architecture
- Service discovery with Eureka
- Independent scaling
- Database per service pattern
- RESTful inter-service communication

---

## 🏛️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Eureka Server (8761)                          │
│                   Service Discovery & Registry                   │
└─────────────────────────────────────────────────────────────────┘
                              ▲
                              │ Register & Discover
                              │
┌─────────────────────────────────────────────────────────────────┐
│                   Scheduler Service (8085)                       │
│              Orchestrates the Complete Workflow                  │
│   ┌──────────────────────────────────────────────────────┐     │
│   │  Every 2 hours (Cron: 0 0 */2 * * *)                 │     │
│   │  1. Fetch News → 2. Generate Tweets →                │     │
│   │  3. Post to Twitter → 4. Send Notifications          │     │
│   └──────────────────────────────────────────────────────┘     │
└─────────────────────────────────────────────────────────────────┘
          │                │               │              │
          ▼                ▼               ▼              ▼
    ┌─────────┐    ┌──────────┐    ┌──────────┐   ┌──────────┐
    │  News   │    │    AI    │    │ Twitter  │   │ Notifier │
    │ Service │    │ Service  │    │ Service  │   │ Service  │
    │  8081   │    │   8082   │    │   8083   │   │   8084   │
    └─────────┘    └──────────┘    └──────────┘   └──────────┘
         │              │                │              │
         ▼              ▼                ▼              ▼
    ┌────────────────────────────────────────────────────────┐
    │              PostgreSQL Databases                       │
    │  news_db | ai_db | twitter_db | notifier_db           │
    └────────────────────────────────────────────────────────┘
         │              │                │
         ▼              ▼                ▼
    NewsData.io    Gemini AI      Twitter API
```

### Service Descriptions

| Service | Port | Purpose | Database |
|---------|------|---------|----------|
| **Eureka Server** | 8761 | Service discovery and registration | - |
| **News Service** | 8081 | Fetch and manage trending news | news_db |
| **AI Service** | 8082 | Generate sarcastic tweets using Gemini | ai_db |
| **Twitter Service** | 8083 | Post tweets to Twitter | twitter_db |
| **Notifier Service** | 8084 | Send notifications | notifier_db |
| **Scheduler Service** | 8085 | Orchestrate the complete workflow | - |

---

## 🛠️ Technologies

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.4.0** - Application framework
- **Spring Cloud 2023.0.3** - Microservices framework
- **Netflix Eureka** - Service discovery
- **OpenFeign** - Declarative REST client
- **Spring Data JPA** - Data access layer
- **Hibernate** - ORM framework

### Database
- **PostgreSQL 15** - Primary database

### External APIs
- **NewsData.io** - News aggregation
- **Google Gemini AI** - Content generation
- **Twitter API v2** - Social media posting

### DevOps
- **Maven** - Build tool
- **Docker & Docker Compose** - Containerization
- **Lombok** - Code generation
- **Spring Boot Actuator** - Monitoring

---

## 📦 Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Docker & Docker Compose** (optional)
- **API Keys:**
  - [NewsData.io API Key](https://newsdata.io/register)
  - [Google Gemini API Key](https://makersuite.google.com/app/apikey)
  - [Twitter Developer Account](https://developer.twitter.com/) (with OAuth 1.0a credentials)

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/sarcastic-tweet-bot.git
cd sarcastic-tweet-bot
```

### 2. Project Structure

```
sarcastic-tweet-bot/
├── eureka-server/
├── news-service/
├── ai-service/
├── twitter-service/
├── notifier-service/
├── scheduler-service/
├── docker-compose.yml
├── .env
└── README.md
```
