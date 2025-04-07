# 🏙️ Blog, Rating & Review, voucher shopping System

A high-performance **review system** built with **Spring Boot**, integrated with **Redis**, **RabbitMQ**, **MyBatis-Plus**, **Nginx proxy**, and **Docker deployment**. It supports high-concurrency scenarios such as **flash sales**, **login**, and **caching**, ensuring scalability and reliability.

---

## 📚 Tech Stack

| Technology    | Description                                        |
|---------------|----------------------------------------------------|
| Spring Boot   | Core Java framework                                |
| MyBatis-Plus  | ORM framework to simplify CRUD operations          |
| Redis         | High-performance cache, login state, distributed lock |
| RabbitMQ      | Asynchronous message queue to handle high concurrency in flash sales |
| Nginx         | Static resource proxy and interface forwarding     |
| Vue.js        | Front-end UI & interaction                         |
| Docker        | Containerized project deployment                   |

---

## 🚀 Core Features

### ✅ Login & Verification Code
- Uses **Redis** to store verification codes
- Supports **SMS-based login**, stores login information in Redis and generates token

### ✅ User Information Caching
- Handles **cache penetration** and **cache breakdown** using **mutex locks + logical expiration**

### ✅ Nearby Shop Location
- Uses **Redis GEO** features to implement nearby shop recommendation

### ✅ Flash Sale System
- Leverages **Redis + Lua scripting** to verify flash sale eligibility
- Uses **RabbitMQ** for asynchronous order processing, avoiding **overselling** and **duplicate orders**



## Optimization 1: Login Module
Current Implementation: Using personal user email to send verification codes via SMTP protocol.

## Optimization 2: Flash Sale (Seckill) Enhancement
High Concurrency Solution: Implemented RabbitMQ message queue to optimize flash sale order processing, significantly reducing database pressure.

### Uses Lua scripts to ensure atomic execution of:
Inventory availability check
User order verification
Inventory deduction
Order creation and user event saving
Asynchronous Processing: Leverages RabbitMQ for asynchronous handling of high-concurrency requests.

## Optimization 3: Rate Limiting Implementation
Token Bucket Algorithm: Applied to control request rates during flash sale events.
