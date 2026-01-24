
# One-line STAR-Based Interview Answers

1. **How did you scale Kafka during peak hours?**  
   When event traffic spiked (S), I increased partitions and enabled autoscaling brokers (A), reducing lag from minutes to seconds (R).

2. **How did you handle S3 write bottlenecks?**  
   During peak ingestion (S), I optimized batch size and parallel writers (A), boosting throughput by ~3× (R).

3. **How did you scale Spark Streaming for high throughput?**  
   With evening spikes (S), I enabled dynamic executor autoscaling (A), eliminating backlog completely (R).

4. **How did you optimize Spark ETL batch performance?**  
   Heavy daily loads slowed ETL (S), so I partitioned inputs and auto-scaled executors (A), cutting job time by ~40% (R).

5. **How did you ensure Elasticsearch query latency remained low?**  
   Queries slowed as data grew (S), so I optimized mappings and rebalanced shards (A), improving latency by 60% (R).

6. **How did you scale Elasticsearch as data increased?**  
   With fast-growing indices (S), I implemented ILM and index rollover (A), stabilizing performance at scale (R).

7. **How did you ensure shard allocation was balanced?**  
   After node imbalance (S), I enabled routing rebalancing and fixed disk watermark issues (A), restoring uniform shard spread (R).

8. **How did you manage peak vs. off-peak infrastructure scaling?**  
   Traffic fluctuated daily (S), so I enabled autoscaling for Kafka + Spark (A), reducing costs while meeting SLAs (R).

9. **How did you reduce ingest-to-dashboard latency?**  
   Freshness delays were high (S), so I parallelized ETL stages (A), reducing latency from hours to minutes (R).

10. **How did you reprocess historical data?**  
    Bad upstream data (S) required replay, so I designed partition-level reprocessing (A), enabling clean rebuilds within hours (R).

11. **How did you ensure exactly-once or clean processing?**  
    Duplicates appeared (S), so I used idempotent Kafka writes + deterministic ETL outputs (A), ensuring clean ingestion (R).

12. **How did you improve dashboard performance?**  
    Heavy queries slowed dashboards (S), so I added pre-aggregated indices (A), improving response times by 70% (R).

13. **How did you improve fault tolerance?**  
    Frequent failures (S), so I added DLQs + checkpointing (A), achieving fast recovery with no data loss (R).

14. **How did you design for scalability?**  
    Data kept growing (S), so I used Kafka–S3–Spark–ES decoupling (A), enabling linear horizontal scaling (R).

15. **How did you optimize S3 storage costs?**  
    Costs rising (S), added lifecycle policies + compression (A), saving ~35% (R).

16. **How did you reduce Spark small-file issues?**  
    Small files slowed ETL (S), consolidated outputs (A), improving job speed by 30% (R).

17. **How did you handle ES indexing failures?**  
    Bulk writes failing (S), tuned retry logic + batch size (A), stabilizing indexing (R).

18. **How did you ensure data quality?**  
    Inconsistent metrics (S), added validation at L0–L3 (A), ensuring accurate analytics (R).

19. **How did you reduce operational overhead?**  
    Manual scaling burdensome (S), automated autoscaling + alerts (A), reducing ops load drastically (R).

20. **How did you improve observability?**  
    Hard to debug delays (S), unified Kafka lag, Spark metrics, ES slow logs in Grafana (A), enabling proactive issue detection (R).
