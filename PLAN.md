# PLAN.md

## Repository Role

Auth Service. This is inferred from the `Auth` service name, `AuthApplication`, and Gradle project description. Current implementation evidence is minimal in this checkout.

## Repository Type

backend

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Gradle
- Spring Data JPA / JDBC
- PostgreSQL
- Lombok

## Main Directories

- `src/main/java/com/example/auth`: Auth service application package.
- `src/main/resources`: application configuration.
- `src/test/java/com/example/auth`: service tests.
- `docs`: service documentation.
- `docs/common`: shared development, review, issue, and convention guides.
- `gradle/wrapper`: Gradle wrapper files.

## Domain Responsibilities

- Authentication service boundary, inferred from service naming.
- No controller, service, repository, entity, or DTO implementation directories were found under `src/main/java` in this checkout.

## API Source of Truth

Backend API specifications are managed in Apidog.

The local repository-side API spec is `API_SPEC.yaml`.

The SprintOps Agent compares Apidog API specs with this repository's implementation state.

## Completion Rule

A backend feature is considered completed when:

- the API exists in Apidog or OpenAPI spec
- matching backend implementation exists
- related PR is merged into the main branch

If there is an open PR but no merge, mark it as in progress.

If the API exists but no implementation evidence exists, mark it as missing.

If implementation exists but no API spec exists, mark it as spec mismatch.

## SprintOps Agent Checkpoints

The SprintOps Agent should inspect:

- `API_SPEC.yaml`
- `src/main/java/com/example/auth`
- `src/main/resources`
- `src/test/java/com/example/auth`
- `docs`
- merged PRs
- open PRs
- issues
- branches
- this `PLAN.md`

## Notes

- Treat this service as implementation-light until controller/service/repository/entity/DTO evidence appears.
- Inspect PRs and branches carefully because implementation may exist outside the current main branch.
