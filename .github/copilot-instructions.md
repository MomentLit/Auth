# Copilot Review Instructions

## General Rules

* Always respond in Korean when performing code reviews.
* When pointing out issues, explain both the reason and the suggested improvement.
* Prioritize real-world concerns such as bugs, maintainability, transaction handling, and testability over simple style issues.

## Project Context

* This repository is responsible for a single service in an MSA (Microservice Architecture) environment.
* Review the code with clear service boundaries and service responsibilities in mind.
* Be careful about unnecessary coupling with other services.

## Test Code

* If there are no tests for newly added or modified logic, explicitly recommend adding test code.
* Review whether the current implementation is testable.
* Suggest unit tests and integration tests when necessary.

## Transaction & Persistence

* Carefully review transaction boundaries and transaction usage.
* Check for potential data consistency issues.
* Watch for unnecessary transactions or missing transactions.
* Review JPA/ORM usage carefully to avoid performance or persistence issues.

## Code Review Focus

Focus especially on:

* Transaction handling
* Service responsibility separation
* Test coverage
* Maintainability
* Security and validation
* Readability
* Potential side effects

## Avoid

* Business logic inside controllers
* Overly coupled code
* Hidden side effects
* Missing validation
* Missing exception handling
* Untested critical logic
