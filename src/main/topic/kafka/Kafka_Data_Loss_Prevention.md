# Kafka Data Loss Prevention Guide

## 1. How do you ensure there is no data loss in Kafka while consuming from a topic?

**Explanation:**
When a consumer reads messages from Kafka, the main concern for data loss is **acknowledgment (offset commit)**. If a consumer reads a message but crashes before committing the offset, Kafka may resend the message or you may lose track of what was processed.

**How to ensure no data loss:**
1. Enable `enable.auto.commit=false` and manage offsets manually.
2. Commit offsets after processing messages successfully.
3. Use idempotent operations on the consumer operation if messages are reprocessed.

**Example:**
```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("group.id", "test-group");
props.put("enable.auto.commit", "false"); // manual commit
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
consumer.subscribe(Arrays.asList("my-topic"));

try {
    while (true) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
        for (ConsumerRecord<String, String> record : records) {
            // Process record
            System.out.println(record.value());
        }
        // Commit offset after successful processing
        consumer.commitSync();
    }
} finally {
    consumer.close();
}
```

---

## 2. How do you ensure there is no data loss in Kafka while the consumer is down?

**Explanation:**
Kafka stores messages in durable logs, so messages are **not deleted until they expire** (based on retention policies). If a consumer is down, it can resume consuming messages from the last committed offset once it comes back.

**How to ensure:**
1. Set **retention period** high enough (e.g., `retention.ms`).
2. Consumers should use automatic or manual offset management to resume from last committed position.
3. Use **consumer groups** to ensure load balancing and fault tolerance.

**Example:**
Consumer crashes after consuming messages but before committing offsets. On restart, it resumes from the **last committed offset**.

```java
consumer.commitSync(); // ensures progress is saved
```

---

## 3. How do you ensure there is no data loss in Kafka while the producer is down?

**Explanation:**
The risk is messages **not being sent or acknowledged**. Kafka provides **producer acknowledgments** to handle this.

**How to ensure:**
1. Enable `acks=all` → waits for all in-sync replicas (ISR) to acknowledge.
2. Enable `retries` → retry sending failed messages.
3. Enable `idempotence` (`enable.idempotence=true`) → ensures no duplicate messages.

**Example:**
```java
Properties props = new Properties();
props.put("bootstrap.servers", "localhost:9092");
props.put("acks", "all");
props.put("retries", 3);
props.put("enable.idempotence", "true");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

KafkaProducer<String, String> producer = new KafkaProducer<>(props);

ProducerRecord<String, String> record = new ProducerRecord<>("my-topic", "key1", "value1");
producer.send(record, (metadata, exception) -> {
    if (exception != null) {
        exception.printStackTrace();
    } else {
        System.out.println("Message sent successfully");
    }
});
producer.close();
```

---

## 4. How do you ensure there is no data loss in Kafka while a broker is down?

**Explanation:**
Kafka uses **replication** to handle broker failures. Each partition has a **leader** and **followers**.

**How to ensure:**
1. Set **replication factor > 1**.
2. Configure **min.insync.replicas > 1** → ensures messages are only acknowledged when replicated.
3. Producers should use `acks=all`.

**Example:**
- Topic with replication factor = 3
- `min.insync.replicas = 2`
- Producer with `acks=all`
Even if 1 broker goes down, messages are still safe on the remaining replicas.

---

## 5. How do you ensure there is no data loss in Kafka while Zookeeper is down?

**Explanation:**
Zookeeper manages **cluster metadata** and **leader elections**. Kafka itself stores data on brokers.

**How to ensure:**
1. Kafka brokers continue serving reads/writes even if Zookeeper is temporarily unavailable.
2. Ensure Zookeeper quorum is maintained (multiple nodes).
3. For production, migrate to **Kafka KRaft mode** (Kafka’s own metadata management).

**Example:**
- Zookeeper temporarily down, brokers continue sending/receiving messages.
- When Zookeeper comes back, metadata is synchronized.

---

## Summary Table

| Scenario                         | Key Configurations / Mechanism                                    |
|---------------------------------|------------------------------------------------------------------|
| Consumer reading messages        | `enable.auto.commit=false`, manual offset commit after processing |
| Consumer down                    | Retention period, resume from last committed offset              |
| Producer down                    | `acks=all`, `retries>0`, `enable.idempotence=true`              |
| Broker down                      | Replication factor >1, `min.insync.replicas>1`, `acks=all`      |
| Zookeeper down                   | Kafka brokers continue; ensure ZK quorum, migrate to KRaft      |
