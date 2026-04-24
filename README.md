# 🔐 Identity Provider (IdP)

> 🛡️ A self-hosted OAuth2 Authorization Server built with **Spring Boot**, **Spring Authorization Server**, and **PostgreSQL** — designed as a reusable authentication backbone for personal microservices projects.

---

## 🚀 Tech Stack

| Category        | Technologies                                                                                                                                                                                                                      |
|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Backend**     | ![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white) ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)                             |
| **Security**    | ![OAuth2](https://img.shields.io/badge/OAuth2-4285F4?style=for-the-badge&logo=google&logoColor=white) ![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)                      |
| **ORM & DB**    | ![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)  |
| **Migrations**  | ![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=for-the-badge&logo=flyway&logoColor=white)                                                                                                                             |
| **Utilities**   | ![Lombok](https://img.shields.io/badge/Lombok-A50?style=for-the-badge)                                                                                                                                                            |

---

## 📝 Description

**Identity Provider** is a standalone OAuth2 Authorization Server intended to serve as a shared authentication layer across personal microservices projects. Resource servers can delegate all authentication and token validation to this IdP instead of managing their own security logic.

It supports two grant type flows:

- 🤝 **Client Credentials** — for service-to-service communication
- 🔑 **Custom Password Grant** — for user-facing applications where username/password authentication is needed

The server issues **JWT access tokens** with role-based claims, persists all client registrations and token data in **PostgreSQL**, and uses **Flyway** for schema management. A protected admin endpoint allows dynamic registration of new OAuth2 clients at runtime.

---

## 🔐 Environment Variables

| Variable                | Description               | Example                                      |
|-------------------------|---------------------------|----------------------------------------------|
| `POSTGRES_USERNAME_PROD`| Database username         | `postgres`                                   |
| `POSTGRES_PASSWORD_PROD`| Database password         | `password`                                   |

---
