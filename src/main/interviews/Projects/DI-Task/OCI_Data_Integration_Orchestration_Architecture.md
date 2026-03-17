# OCI Data Integration – Task Service (Orchestration Service) Architecture

## Overview
OCI Data Integration (OCI DI) provides a fully managed **orchestration service** for building, scheduling, and operating end-to-end data pipelines.  
This document explains the **architecture** of the Task (Orchestration) Service in terms of **What**, **Why**, and **How**.

---

## 1. What is the Task (Orchestration) Service?

The Task Service is the **control plane** of OCI Data Integration that:
- Coordinates design & execution of different task types
- Manages dependencies and sequencing
- Handles scheduling, retries, and failure handling
- Delegates actual processing to the appropriate execution engines

It acts as a **workflow engine for data pipelines**, similar in spirit to Airflow or Informatica, but fully managed and OCI-native.

---

## 2. Why This Architecture Exists

### Key Problems It Solves
- Running ETL jobs in isolation does not scale
- Different workloads need different compute engines
- Operational concerns (retry, monitoring, alerting) must be standardized
- Teams need separation between design-time and runtime

### Architectural Goals
- Separation of **design**, **execution** and **orchestration**
- Elastic scalability without infrastructure management
- Consistent operational behavior across task types
- Tight integration with OCI services (Events, Logging, Monitoring)

---

## 3. High-Level Architecture

```
+---------------------+
|   Design & Build    |
|---------------------|
| Workspace           |
| - Data Assets       |
| - Data Flows        |
| - Tasks             |
| - Pipelines         |
+----------+----------+
           |
           v
+---------------------+
|      Publish        |
|---------------------|
| Application         |
| - Versioned runtime |
| - Validated DAG     |
+----------+----------+
           |
           v
+-----------------------------+
| Runtime Orchestration Engine|
|-----------------------------|
| - Dependency resolution     |
| - Sequencing & parallelism  |
| - Parameter injection       |
| - Retry & error handling    |
+----------+------------------+
           |
           v
+---------------------------------------------+
|              Execution Layer                |
|---------------------------------------------|
| Data Loader Task  -> DB native engine        |
| Integration Task  -> OCI DI engine           |
| Data Flow Task    -> OCI Data Flow (Spark)   |
| SQL / REST Task   -> External systems        |
+---------------------------------------------+
           |
           v
+-----------------------------+
| Monitoring & Management     |
|-----------------------------|
| OCI Logging                 |
| OCI Monitoring              |
| OCI Events & Alerts         |
+-----------------------------+
```

---

## 4. Core Components (What & How)

### 4.1 Workspace (Design-Time)
- Logical container for development
- Holds metadata, task definitions, pipelines
- Used only for design and validation

### 4.2 Tasks
Smallest executable unit of work.

| Task Type | Purpose | Execution Engine |
|----------|--------|------------------|
| Data Loader Task | Bulk data load | Database engine |
| Integration Task | Complex ETL | OCI DI engine |
| OCI Data Flow Task | Big data processing | Spark (OCI Data Flow) |
| SQL / REST Task | Control & integration | External systems |

### 4.3 Pipelines (Orchestration Layer)
- DAG-based workflow definition
- Supports:
  - Sequential and parallel execution
  - Conditional branching
  - Parameters
  - Error handling paths

### 4.4 Application (Runtime Container)
- Published, immutable runtime version
- Required for execution
- Separates dev and prod concerns

---

## 5. Execution Flow (How It Works End-to-End)

1. **Design**
   - Create data assets, data flows, tasks
   - Assemble tasks into pipelines

2. **Publish**
   - Compile tasks into an Application
   - Validate dependencies and configuration

3. **Trigger**
   - Manual run
   - Scheduled execution
   - Event-based trigger
   - REST / CLI invocation

4. **Orchestration**
   - Runtime engine resolves DAG
   - Executes tasks in correct order
   - Handles retries and failures

5. **Execution**
   - Tasks run on delegated compute engines
   - Orchestrator tracks state and metrics

6. **Monitoring**
   - Logs and metrics emitted to OCI services
   - Alerts raised on failure or SLA breach

---

## 6. Architectural Principles

- **Control plane vs Data plane separation**
- **Compute delegation**, not compute ownership
- **Declarative pipeline definition**
- **Cloud-native observability**
- **Security via OCI IAM and Vault**

---

## 7. When to Use Which Task

- Data Loader Task → Fast ingestion, minimal logic
- Integration Task → Business ETL, joins, transformations
- OCI Data Flow Task → Massive scale, Spark workloads

---

## 8. Summary

The OCI Data Integration Task Service acts as:
- A **workflow orchestrator**
- A **runtime coordinator**
- A **bridge between design and execution**

By separating orchestration from execution, OCI DI achieves scalability, reliability, and operational simplicity for enterprise data pipelines.