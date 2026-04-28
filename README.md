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

## 💡 Interesting Techniques

- **Custom Password Grant Type** — A fully custom `password` grant flow is implemented via [PasswordGrantAuthenticationConverter](src/main/java/pl/sebastianklimas/idp/auth/PasswordGrantAuthenticationConverter.java) (extracts credentials from the HTTP request), [PasswordGrantAuthenticationToken](src/main/java/pl/sebastianklimas/idp/auth/PasswordGrantAuthenticationToken.java) (carries unauthenticated and authenticated states), and [PasswordGrantAuthenticationProvider](src/main/java/pl/sebastianklimas/idp/auth/PasswordGrantAuthenticationProvider.java) (validates credentials and generates the token). The provider always returns `OAuth2AccessTokenAuthenticationToken` so that the `OAuth2TokenEndpointFilter` can correctly build the HTTP response.

- **Multi-Chain `SecurityFilterChain` Architecture** — Three separate filter chains are defined with `@Order` to cleanly separate concerns: the `@Order(1)` chain handles all OAuth2 endpoints via `getEndpointsMatcher()`, while the `@Order(2)` chain scopes the `/admin/**` endpoint and restricts access to localhost only using `IpAddressMatcher` with OR logic for both `127.0.0.1` and `::1` (IPv6 loopback).

- **IP-Restricted Admin Endpoint** — The `/admin/register-client` endpoint is only accessible from the local machine. Access control is implemented with a custom `AuthorizationManager` lambda that inspects `HttpServletRequest` directly, without requiring any authentication mechanism.

- **Cryptographically Secure Client Secret Generation** — When registering a new OAuth2 client, the [AdminService](src/main/java/pl/sebastianklimas/idp/admin/AdminService.java) generates a 32-byte random secret using `SecureRandom`, encodes it as Base64URL, and returns it once in plaintext. Only the BCrypt hash is stored, making it impossible to recover the secret after initial registration.

- **JDBC-backed OAuth2 Persistence** — Both `RegisteredClientRepository` and `OAuth2AuthorizationService` use their JDBC implementations (`JdbcRegisteredClientRepository`, `JdbcOAuth2AuthorizationService`), ensuring full persistence of clients and issued tokens in PostgreSQL across restarts.

- **Delegating Token Generator** — The `OAuth2TokenGenerator` is assembled as a `DelegatingOAuth2TokenGenerator` composed of `JwtGenerator`, `OAuth2AccessTokenGenerator`, and `OAuth2RefreshTokenGenerator`, providing flexible token type support from a single bean.

- **User Enumeration Prevention** — In [PasswordGrantAuthenticationProvider](src/main/java/pl/sebastianklimas/idp/auth/PasswordGrantAuthenticationProvider.java), both "user not found" and "wrong password" paths throw the same `OAuth2AuthenticationException(INVALID_GRANT)`, preventing attackers from inferring whether a given email exists in the system.

---

## 🧩 Project Structure

```
/src
  /main/java/pl/sebastianklimas/idp
    /admin         → Admin controller, service, and DTOs for client registration
    /auth          → Custom password grant converter, token, and provider
    /config        → Authorization server, security filter chains, and password encoder config
    /users         → User and Role entities, UserRepository
  /resources
    /db/migration  → Flyway SQL migration scripts
    application.yml
    application-prod.yml
```

- **/admin**: Exposes a localhost-only REST endpoint to dynamically register new OAuth2 clients at runtime.
- **/auth**: Full implementation of the custom `password` grant type — converter, token, and authentication provider.
- **/config**: Split into [AuthorizationServerConfig](src/main/java/pl/sebastianklimas/idp/config/AuthorizationServerConfig.java) (token infrastructure beans), [SecurityConfig](src/main/java/pl/sebastianklimas/idp/config/SecurityConfig.java) (filter chains), and [PasswordConfig](src/main/java/pl/sebastianklimas/idp/config/PasswordConfig.java) (BCrypt encoder).
- **/users**: JPA entities for [User](src/main/java/pl/sebastianklimas/idp/users/User.java) and [Role](src/main/java/pl/sebastianklimas/idp/users/Role.java) with a `ManyToMany` join, and a [UserRepository](src/main/java/pl/sebastianklimas/idp/users/UserRepository.java) used by the password grant provider.
- **/db/migration**: Flyway scripts managing `users`, `roles`, `user_role`, `oauth2_registered_client`, and `oauth2_authorization` tables.

---

## 🏫 Notable Technologies and Libraries

- **Spring Authorization Server** — Provides the OAuth2/OIDC server infrastructure, token endpoint, and client repository abstractions.
- **Spring Security** — Underpins the multi-chain filter configuration, IP-based access control, and authentication architecture.
- **Nimbus JOSE + JWT** — Used via `NimbusJwtEncoder` for signing and encoding JWT access tokens with a `JWKSource`.
- **Flyway** — Manages all database schema migrations for both application tables and required Spring Authorization Server tables.
- **Lombok** — Reduces boilerplate across DTOs and service classes with `@Getter`, `@Setter`, `@AllArgsConstructor`, and `@RequiredArgsConstructor`.
- **PostgreSQL** — Primary database for persisting users, roles, registered clients, and issued authorizations.

---

## 🧠 Key Features

✅ Custom `password` grant type with full Spring Authorization Server integration  
✅ Client Credentials flow for service-to-service authentication  
✅ JWT access tokens with role-based claims  
✅ JDBC-persisted clients and tokens (survives restarts)  
✅ IP-restricted admin API for dynamic client registration  
✅ Cryptographically secure client secret generation  
✅ User enumeration protection  
✅ Flyway-managed schema with Spring Authorization Server tables  
✅ Designed as a reusable IdP for personal microservices ecosystems
