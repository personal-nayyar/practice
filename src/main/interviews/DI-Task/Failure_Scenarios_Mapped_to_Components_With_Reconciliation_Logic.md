# Failure Scenarios Mapped to Components — With Reconciliation Logic

This document explains **how task reconciliation operates across components** in OCI Data Integration to handle distributed failure scenarios and ensure **correct, exactly-once pipeline execution**.

---

## Overview

In OCI Data Integration, orchestration and execution are decoupled:
- The **control plane** defines the desired state of tasks and pipelines.
- The **data plane** performs actual execution.
- A **reconciliation service** continuously aligns actual execution with desired state.

Failures are treated as normal operating conditions and are handled deterministically through reconciliation.

---

## Failure Scenarios and Reconciliation Behavior

| Failure Scenario | Where It Happens | What Goes Wrong | How Reconciliation Detects It | Corrective Action | Final Guarantee |
|-----------------|-----------------|----------------|-------------------------------|-------------------|-----------------|
| **Orchestrator crash during execution** | Orchestration Engine (Control Plane) | In-memory orchestration state is lost while tasks continue running | On restart, reconciliation scans RUNNING tasks from the state store | Queries execution engine; updates task state to COMPLETED or FAILED | Safe recovery with no stuck pipelines |
| **Callback lost after successful execution** | Execution → Orchestration | Execution completes but callback is never received | Detects RUNNING tasks exceeding expected execution window | Queries execution engine and marks task COMPLETED | Eliminates zombie RUNNING tasks |
| **Execution engine crash mid-run** | Execution Engine (Data Plane) | Task fails without reliable failure signal | Missing heartbeat or execution status during reconciliation scan | Marks task FAILED and evaluates retry policy | Automated failure recovery |
| **Retry overlaps with delayed success** | Control Plane | Retry is triggered while original execution completes late | Detects multiple executions for the same task | Accepts first valid completion; ignores duplicates | Exactly-once execution semantics |
| **Premature downstream task scheduling** | Orchestration Engine | Child task scheduled before parent truly completes | Validates DAG invariants during reconciliation | Rolls back downstream scheduling and replays DAG | Correct DAG execution order |
| **Orphaned execution after pipeline abort** | Execution Engine | Execution continues after pipeline termination | Finds execution without active pipeline association | Terminates execution and marks task ABORTED | No resource leaks |

---

## Key Design Insight

Reconciliation is **not traditional error handling**.  
It is a **state correction mechanism** that ensures the system converges toward correctness.

- Desired state comes from orchestration metadata
- Actual state comes from execution engines
- Reconciliation continuously aligns the two

---

## Architectural Guarantees

- Exactly-once task execution
- Idempotent retries
- Deterministic pipeline outcomes
- No zombie or orphaned executions
- Safe recovery from partial failures

---

## Summary

By mapping failure scenarios directly to component responsibilities and reconciliation logic, OCI Data Integration ensures reliable, fault-tolerant orchestration even in highly distributed execution environments.

Reconciliation acts as the **safety net** that guarantees correctness when failures inevitably occur.
