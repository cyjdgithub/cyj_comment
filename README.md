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
