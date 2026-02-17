# Let's Play – Todo List

## Setup
- [x] Write OpenAPI doc for API behavior and schema
- [x] Create initial Spring project
- [x] MongoDB Community Docker

## Schema
- [x] Define MongoDB entities (User, Product)
- [x] Add default admin
- [x] Define DTOs (request/response models aligned with OpenAPI)

## Auth
- [x] Add Auth endpoints
  - [x] Password hashing with BCrypt
  - [x] JWT creation and validation (Spring Security)
  - [x] POST /auth/signup
  - [x] POST /auth/signin

## Users (admin only)
- [ ] Add Users endpoints
  - [ ] Enforce authentication and admin-only authorization
  - [ ] GET /users
  - [ ] POST /users
  - [ ] GET /users/{id}
  - [ ] PUT /users/{id}
  - [ ] DELETE /users/{id}

## Products
- [ ] Add Products endpoints
  - [ ] Enforce access rules (public GET; auth for POST; owner or admin for PUT/DELETE)
  - [ ] GET /products (public)
  - [ ] POST /products
  - [ ] GET /products/{id} (public)
  - [ ] PUT /products/{id}
  - [ ] DELETE /products/{id}

## Error handling
- [ ] Global exception handler
- [ ] Standard error response format `{ "message", "status" }` (per OpenAPI)
- [ ] Map exceptions to appropriate status codes (400, 401, 403, 404, 409)

## Security
- [ ] Exclude password (and other sensitive fields) from all API responses
- [ ] Validate and sanitize inputs (e.g. prevent injection)
- [ ] Implement HTTPS
