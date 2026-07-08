# Service Policy

## Confirmed Policies

- No domain policy is visible in the current code.
- No API behavior is visible in the current code.

## Validation Rules

Visible validation rules are documented from DTO annotations, entity methods, and service methods only. Any validation behavior not present in code is Needs confirmation.

## Authorization Rules

No security configuration visible.

## Creation Policy

Creation behavior is documented only where visible in service or entity factory methods.

## Update Policy

Update behavior is documented only where visible in service or entity update methods.

## Deletion Policy

Deletion behavior is documented only where visible in service or entity delete methods.

## State Transition Rules

State transitions are documented only where visible in entity or service methods. Missing transitions are Needs confirmation.

## Exception Cases

Project-specific exception handling is visible under `com.example.auth.global.exception`.
Visible mappings:

- `BadRequestException`: `400 Bad Request`
- `UnauthorizedException`: `401 Unauthorized`
- `TokenNotFoundException`: `401 Unauthorized`
- `GoogleOauthException`: `502 Bad Gateway`
- Other `AuthException`: `500 Internal Server Error`
- Other `Exception`: `500 Internal Server Error`

## API Behavior Policy

- API behavior must be documented in `API_SPEC.yaml`.
- If API behavior changes, `API_SPEC.yaml` must be updated in the same PR.

## Needs Confirmation

- Service responsibility is not visible from code.
- API contract is not yet defined.
- Database schema and persistence policy are not visible.
- Authentication and authorization behavior are not visible.
