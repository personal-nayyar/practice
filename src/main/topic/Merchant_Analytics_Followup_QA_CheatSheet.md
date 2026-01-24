
# Merchant Analytics — Follow‑Up Q&A Cheat Sheet

This cheat sheet provides concise answers to common follow‑up questions asked after presenting the Merchant Analytics system design.

---

## **1. Why did you choose Kafka over other message queues?**
Kafka offers horizontal scalability, high throughput, ordering per key, durability, and replay capabilities essential for payment event ingestion.

## **2. How do you ensure exactly‑once or clean processing?**
Idempotent Kafka producers, deterministic event keys, Spark checkpointing, and atomic S3 writes ensure clean, duplicate‑free processing.

## **3. How do you handle late or out‑of‑order events?**
Spark Structured Streaming handles watermarking, and ETL pipelines re-read hourly partitions from S3 to correct late data.

## **4. Why store data in S3 before ETL instead of processing directly?**
S3 creates a durable source‑of‑truth, decouples ingestion from compute, enables reprocessing, and avoids overwhelming Elasticsearch.

## **5. How do you scale Spark Streaming during peak hours?**
Dynamic executor autoscaling + Kubernetes/EMR autoscaling adds executors when Kafka lag grows, and scales down during off‑peak hours.

## **6. How do you scale Spark ETL pipelines?**
Increase parallel partitions, autoscale compute nodes, optimize partition pruning, and run ETL in smaller incremental batches.

## **7. How do you prevent S3 write bottlenecks?**
Use large batch writes, multipart uploads, random prefixes, S3A committers, and scale writers during peaks.

## **8. How do you handle small files in S3?**
Repartition/coalesce before writing and run compaction jobs to merge small files for efficient downstream reads.

## **9. How do you scale Elasticsearch as data grows?**
Add nodes, optimize shard sizing, use index rollover, ILM hot‑warm‑cold storage, and pre‑aggregation to reduce query load.

## **10. What if Elasticsearch queries become slow?**
Optimize fields/mappings, filter early, reduce cardinality, rebalance shards, cache expensive results, and profile slow queries.

## **11. How do you ensure shard allocation stays balanced?**
Enable routing allocation, remove restrictive node rules, monitor disk watermark limits, and trigger cluster rebalance when needed.

## **12. How do you reprocess historical data?**
Trigger ETL on specific S3 partitions, regenerate L0→L3 datasets, and reindex processed data without touching live ingestion.

## **13. What if Kafka lag continuously increases?**
Add partitions, scale consumers/executors, optimize producer batching, and ensure brokers have sufficient network/disk capacity.

## **14. How do you guarantee schema evolution?**
Use a Schema Registry (Avro/Protobuf) with backward compatibility and versioned schemas.

## **15. How do you ensure the dashboard gives fast responses?**
Use ES aggregations, domain‑specific indices, caching in the query layer, and pre‑aggregated L3 datasets for OLAP‑style queries.

---

## **Quick 10‑Second Summary**
Kafka for scalable ingestion, S3 as durable data lake, Spark for streaming + batch ETL, Elasticsearch for fast analytics, and autoscaling everywhere to handle payment peaks.

---

