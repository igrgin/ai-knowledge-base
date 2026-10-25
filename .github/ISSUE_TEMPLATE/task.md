---
name: Planned task
about: Define demonstrable behavior with separate start and completion gates
title: ''
labels: 'feature'
assignees: ''
---

## Lighthouse source

Task ID and current `.Lighthouse/plan.md` / `spec.md` sections; exactly one bug/feature/spike intent label. Check current `.Lighthouse/plan.md` issue links before creating a duplicate issue.

## Demonstrable behavior

Describe the user-visible outcome and its UI, API, persistence and failure behavior.

## Start gates

Name completed tasks or agreed contract checkpoints that allow work to begin. Publication of a draft contract alone does not satisfy a checkpoint.

## Completion prerequisites

Name real integrated dependencies and evidence required to finish. These are not necessarily start gates.

## Meaningful tests

Describe observable behavior and consequential failure cases. Record actual commands/results when executed; ordinary CI makes no paid AI calls.

## Ownership, coordination and reuse

Name the slice role, permitted paths, schema ownership, provider/consumer contracts and existing implementation to extend. Shared files and migration numbering belong to the integrator. GitHub assignee and implementation-agent role are distinct.

## Authorization and delivery

Implementation needs separate owner authorization. Preserve current task concurrency. Use the issue-number/title branch convention, a linked PR, independent technical review and manual owner approval/merge.

For an interactive spike, replace implementation/delivery sections with the question, boundaries, evidence and human-agreed decision finish line. No implementation-agent handoff or mandatory PR applies; verify the final decision comment on the issue before completion. Labeling a task supplies no execution authority.
