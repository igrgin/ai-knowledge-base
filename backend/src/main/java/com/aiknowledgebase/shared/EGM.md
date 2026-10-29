# Shared application errors

## Overview

This package defines typed application failures without Spring or HTTP types.
Its [web adapter](web/EGM.md) translates those failures into HTTP responses.
[K0](../../../../../../../docs/contracts/k0-foundation.md#safe-mvc-errors)
owns the canonical error catalogue and consumer conventions; the
[architecture map](../../../../../../../EGM.md) locates the broader system.

## Interface

| Symbol | Contract |
| --- | --- |
| [`ApplicationException(ErrorCode)`](ApplicationException.java) | Public final `RuntimeException` subtype for a deliberate application failure; accepts a non-null error code. |
| [`ApplicationException.code()`](ApplicationException.java) | Returns the supplied `ErrorCode`. |
| [`ErrorCode`](ErrorCode.java) | Public enum of application-defined codes and their fixed safe messages. |
| [`ErrorCode.message()`](ErrorCode.java) | Returns the message associated with that enum constant. |
| [Web package](web/EGM.md) | Owns the public JSON envelope, HTTP mappings and request correlation. |

## Invariants

- [`ApplicationException`'s constructor](ApplicationException.java) rejects a
  null code and stores the code in a final field. Its exception message is the
  enum name, not the public explanatory message.
- [`ErrorCode`](ErrorCode.java) stores a fixed message per constant. Neither
  direct source file imports Spring or HTTP types; status mapping belongs to
  [`ApiExceptionHandler`](web/ApiExceptionHandler.java).
- [K0's extension rule](../../../../../../../docs/contracts/k0-foundation.md#safe-mvc-errors)
  requires distinct client behavior to justify a new code. Its web mapping and
  affected consumers must be updated together. This is a maintenance obligation,
  not runtime validation performed by this package.

The [trusted-identity](../../../../../../../docs/contracts/k0-foundation.md#identifiers-and-trusted-identity)
and [transaction](../../../../../../../docs/contracts/k0-foundation.md#jdbc-transactions)
sections of K0 are obligations for feature implementations. These classes provide
no authenticated principal, resource authorization, transaction service or
durable-operation deduplication. Their concrete behavior and tests belong to the
consuming tasks named in K0.

## Side Effects

[`ApplicationException`](ApplicationException.java) and [`ErrorCode`](ErrorCode.java)
perform no database, filesystem, network or logging operations. Throwing an
`ApplicationException` transfers control to the caller's exception handling;
the [web adapter](web/EGM.md#side-effects) owns HTTP and logging effects.

## Verification

[`ApiContractTest`](../../../../../test/java/contracts/ApiContractTest.java)
checks typed application errors through the MVC adapter, including safe bodies,
status mappings and correlation. It does not prove future feature ownership or
transaction behavior.

From the repository root, with the [documented Java/Node prerequisites](../../../../../../../docs/local-development.md#native-tests):

```sh
./mvnw -pl backend -DskipFrontendTests=true -Dtest=ApiContractTest test
```

This focused command needs no Docker; Maven still installs/builds frontend assets.
See the [web contract](web/EGM.md#verification) for the real HTTP wiring check.
