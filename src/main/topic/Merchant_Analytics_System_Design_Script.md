
# Merchant Analytics — System Design Interview Script

## 1. Problem Context
“Merchant Analytics is a large-scale data processing platform that aggregates payment events from multiple gateways and powers real-time dashboards for merchants. The system needs to ingest high-volume transactional events, process them reliably, store them cost-effectively, and provide fast query responses for analytics.”

## 2. High-Level Architecture
“We built a decoupled architecture using Kafka for ingestion, S3 as the data lake, Spark for both streaming and batch ETL, and Elasticsearch for powering analytical queries. This separation of concerns allows each layer to scale independently.”

**Flow:**  
Payment Gateways → Kafka → Spark Streaming → S3 partitions → Spark ETL (L0→L3) → Elasticsearch → Dashboard

## 3. Ingestion Layer — Kafka
“Payment gateways push events into Kafka. Kafka handles bursty traffic, provides durability, and gives us backpressure control.”

## 4. Storage Layer — S3
“Spark Streaming writes events to S3 in hourly/day/week partitions. S3 provides cheap, durable, scalable storage.”

## 5. ETL Layer — Spark
“Our ETL pipeline runs multiple stages — L0 raw, L1 cleaned, L2 enriched, L3 aggregated. Autoscaling Spark clusters speed up processing.”

## 6. Indexing Layer — Elasticsearch
“Processed L3 datasets are indexed into Elasticsearch with ILM, shard optimization, and fast queries powering dashboards.”

## 7. Serving Layer
“Search APIs translate requests into Elasticsearch aggregations, delivering sub-second dashboard performance.”

## 8. Scalability
“Kafka scales via partitions, Spark via dynamic executors, S3 horizontally, Elasticsearch via more nodes and ILM.”

## 9. Reliability
“DLQs, checkpointing, idempotent ETL writes, and S3 raw data ensure high reliability and easy reprocessing.”

## 10. Observability
“We monitor Kafka lag, Spark delay, ETL runtimes, S3 throughput, ES query latency, and shard health using Grafana.”

## 11. Closing Summary
“Overall, the architecture is highly scalable, cost-efficient, fault-tolerant, and supports near real-time analytics.”
