# Service Overview

## Service Name

Auth

## Service Responsibility

No visible controller endpoints or domain use cases are implemented yet.

## Technology Stack

- Language: Java 21
- Framework: Spring Boot 4.0.5
- Build Tool: Gradle
- Database: PostgreSQL runtime dependency; no persistence code visible
- Other: Lombok is used where visible. JWT and Spring Security are used where security classes are visible.

## Main Package Structure

- Main package: `com.example.auth`
- Controller: No controller package or route annotations found.
- Service: No service classes found.
- Repository: No repository classes found.
- DTO: No DTO classes found.
- Entity/domain: No entity classes found.

## Main Domains

No entities, domain models, controllers, services, or repositories are visible in the current code.

## Main Features

No visible controller endpoints or domain use cases are implemented yet.

## Main APIs

No visible HTTP APIs.

## Data Access Structure

No repository or persistence component is visible. Build dependencies include Spring Data JDBC, Spring Data JPA, and PostgreSQL.

## Exception Handling

No project-specific exception handling is visible.

## Test Structure

Only a Spring Boot context load test is visible.

## API Documentation

This service uses `API_SPEC.yaml` as the main API specification.
When API behavior changes, `API_SPEC.yaml` must be updated in the same PR.

## Development Notes

- Preserve the current single-module service structure.
- Follow the existing package and naming conventions.
- Keep controller, service, repository, entity, and DTO responsibilities separate where those layers exist.
- Do not add cross-service behavior unless it is visible in code or explicitly specified by an Issue.
- If implementation changes API behavior, update `API_SPEC.yaml` in the same PR.

## Needs Confirmation

- Service responsibility is not visible from code.
- API contract is not yet defined.
- Database schema and persistence policy are not visible.
- Authentication and authorization behavior are not visible.
