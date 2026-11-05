# Shared web error adapter

## Overview

This package adapts [shared application errors](../EGM.md) and Spring MVC
failures to the public response defined by
[K0](../../../../../../../../docs/contracts/k0-foundation.md#safe-mvc-errors).
It also establishes server-generated request correlation. It supplies no
authentication or resource-ownership implementation.

## Interface

| Symbol | Contract |
| --- | --- |
| [`ApiError`](ApiError.java) | Public record with `String code`, `String message` and `UUID requestId`, plus its generated constructor/accessors. The record itself does not validate or sanitize supplied values. |
| [`ApiExceptionHandler`](ApiExceptionHandler.java) | Spring-managed `@RestControllerAdvice` extending `ResponseEntityExceptionHandler`; translates application, framework and unexpected MVC exceptions. Its exception hooks are package-private/protected and its response/classification helpers are private. Feature code throws typed application errors rather than calling those hooks. |
| [`RequestIdFilter`](RequestIdFilter.java) | Spring `@Component` extending `OncePerRequestFilter`, ordered at `HIGHEST_PRECEDENCE`; sets response correlation before calling the downstream chain. |
| [`RequestIdFilter.HEADER`](RequestIdFilter.java) | Public `X-Request-ID` header name. |
| [`RequestIdFilter.requestId(HttpServletRequest)`](RequestIdFilter.java) | Returns the UUID already in its request attribute, or generates/stores one when absent. |

## Invariants

- [`RequestIdFilter.requestId`](RequestIdFilter.java) does not trust incoming
  request-ID headers. It shares the request attribute's UUID with error handling.
  [K0](../../../../../../../../docs/contracts/k0-foundation.md#identifiers-and-trusted-identity)
  distinguishes this per-attempt correlation from a feature's durable retry key.
- [`ApiExceptionHandler.respond`](ApiExceptionHandler.java) builds the envelope
  from the enum name, its fixed safe message and the request UUID. It explicitly
  sets `application/json`, including when `Accept` excludes JSON, and copies
  protocol headers. Success-response negotiation is unaffected. It returns
  without replacing an already committed response.
- [`status`](ApiExceptionHandler.java) maps each application code explicitly;
  [`frameworkCode`](ApiExceptionHandler.java) classifies framework failures by
  exception kind. The [K0 catalogue](../../../../../../../../docs/contracts/k0-foundation.md#safe-mvc-errors)
  owns the mappings and special cases. Unknown framework `ErrorResponse`
  failures retain their status and protocol headers under
  `FRAMEWORK_REQUEST_REJECTED`; direct application use of that code maps to 400.
- [`unexpected`](ApiExceptionHandler.java) maps other unhandled exceptions to
  safe HTTP 500 `INTERNAL_ERROR`. Its explicit error log contains only the
  correlation UUID and exception class, excluding exception messages and stacks.
  The method-not-supported override also bypasses the superclass warning that
  includes submitted input. These statements describe this adapter's logging,
  not a guarantee about every logger in the application.

[K0](../../../../../../../../docs/contracts/k0-foundation.md#safe-mvc-errors)
assigns authentication/security-filter responses to T3; they must reuse the
public shape when introduced. This MVC advice does not establish their coverage
or servlet-container error coverage. Actuator health keeps its own representation.

## Side Effects

- [`RequestIdFilter.requestId`](RequestIdFilter.java) can mutate the servlet
  request by storing a generated UUID.
- [`RequestIdFilter.doFilterInternal`](RequestIdFilter.java) sets the response's
  `X-Request-ID` header and invokes the downstream filter chain.
- [`ApiExceptionHandler`](ApiExceptionHandler.java) supplies response bodies,
  headers and statuses to Spring MVC for serialization; its `unexpected` hook
  emits the restricted error log described above. It performs no database,
  filesystem or provider calls.

## Verification

[`ApiContractTest`](../../../../../../test/java/contracts/ApiContractTest.java)
owns error classification, safe messages, headers, correlation and committed
response checks. [`ErrorRepresentationITests`](../../../../../../test/java/contracts/ErrorRepresentationITests.java)
owns the real HTTP restrictive-`Accept` JSON 406 regression, protocol header
and filter/advice correlation. General missing-route and dummy success-format
smoke checks are retired; real feature responses own their success formats.

Run from the repository root after the
[Java/Node and integration prerequisites](../../../../../../../../docs/local-development.md#native-tests):

```sh
./mvnw -pl backend -DskipFrontendTests=true -Dtest=ApiContractTest test
./mvnw -pl backend -Pintegration-tests -DskipUnitTests=true -DskipFrontendTests=true -Dit.test=ErrorRepresentationITests verify
```

The first command is Docker-free; the second requires real PostgreSQL through
Docker. Both still build frontend assets. Actual identity/authorization checks
remain feature-owned; these fixtures do not substitute for T3's integration.
