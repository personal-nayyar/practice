# OCI Data Integration – Task Reconciliation  
## Failure Scenarios Walkthrough

This document describes common **failure scenarios** in the OCI Data Integration Task Service and explains how **task reconciliation** ensures correctness, reliability, and exactly-once execution.

---

## Overview

Task reconciliation is a **control-plane mechanism** that continuously compares the **desired task state** (from orchestration metadata) with the **actual execution state** (from execution engines) and applies corrective actions when inconsistencies are detected.

This is critical in distributed systems where:
- Execution is asynchronous
- Callbacks are best-effort
- Components can crash or restart independently

---

## Failure Scenario 1: Orchestrator Crash During Task Execution

### Situation
- Task is in **RUNNING** state
- Orchestration service crashes or restarts
- Execution engine continues running

### State Divergence
```
Control Plane   : RUNNING
Execution Engine: RUNNING / COMPLETED
```

### Reconciliation Actions
1. On restart, reconciliation scans RUNNING tasks
2. Queries execution engine for actual status
3. If execution completed successfully:
   - Task state updated to **COMPLETED**
   - Downstream tasks scheduled
4. If execution is no longer active:
   - Task marked **FAILED**
   - Retry triggered (if policy allows)

### Outcome
- No stuck pipelines
- Safe recovery after orchestrator restart

---

## Failure Scenario 2: Task Completes but Callback Is Lost

### Situation
- Task execution completes successfully
- Network issue prevents callback to orchestrator
- Task remains in RUNNING state

### State Divergence
```
Control Plane   : RUNNING
Execution Engine: COMPLETED
```

### Reconciliation Actions
1. Reconciliation detects task exceeding expected runtime
2. Queries execution engine directly
3. Confirms successful completion
4. Updates task state to **COMPLETED**
5. Resumes DAG execution

### Outcome
- Prevents zombie RUNNING tasks
- Ensures pipeline progress

---

## Failure Scenario 3: Retry Overlaps with Delayed Success

### Situation
- Task execution is slow
- Timeout triggers retry
- Original execution later completes successfully

### Risk
- Duplicate execution
- Multiple writes to target systems

### Reconciliation Actions
1. Detects multiple execution attempts
2. Applies idempotency rules:
   - Accepts first valid completion
   - Ignores late or duplicate results
3. Marks task **COMPLETED**
4. Cancels or ignores overlapping execution

### Outcome
- Exactly-once semantics preserved
- No data duplication

---

## Failure Scenario 4: Execution Engine Failure Mid-Run

### Situation
- DB or Spark job crashes
- No explicit failure callback is received

### State Divergence
```
Control Plane   : RUNNING
Execution Engine: FAILED
```

### Reconciliation Actions
1. Detects missing heartbeat or execution signal
2. Marks task **FAILED**
3. Evaluates retry policy:
   - Retry available → **RETRYING**
   - Retries exhausted → **ABORTED**

### Outcome
- Automated recovery
- Reduced manual intervention

---

## Failure Scenario 5: Downstream Task Scheduled Prematurely

### Situation
- Parent task temporarily appears completed
- Child task is scheduled
- Parent task later fails

### Reconciliation Actions
1. Validates DAG invariants
2. Detects parent-child inconsistency
3. Rolls back downstream scheduling
4. Retries or fails parent task
5. Replays DAG after successful completion

### Outcome
- DAG correctness preserved
- Prevents partial pipeline execution

---

## Failure Scenario 6: Orphaned Executions After Pipeline Abort

### Situation
- Pipeline is manually aborted
- One or more tasks continue running

### Reconciliation Actions
1. Detects executions without active pipeline
2. Terminates or cleans up orphaned executions
3. Marks tasks as **ABORTED**
4. Releases compute resources

### Outcome
- No resource leaks
- Clean pipeline termination

---

## Key Design Guarantees

- Exactly-once task execution
- Idempotent retries
- No zombie or orphaned tasks
- Safe recovery from crashes
- Deterministic pipeline outcomes

---

## Summary

Task reconciliation acts as a **safety net** for OCI Data Integration pipelines by continuously enforcing consistency between orchestration metadata and runtime execution.  
This enables scalable, fault-tolerant, and production-grade ETL orchestration in distributed environments.
