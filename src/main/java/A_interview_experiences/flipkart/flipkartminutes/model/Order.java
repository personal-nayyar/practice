package A_interview_experiences.flipkart.flipkartminutes.model;

import lombok.Getter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

@Getter
public class Order {
    public enum State { PENDING, ASSIGNED, PICKED_UP, DELIVERED, CANCELLED, AUTO_CANCELLED }

    public final String id;
    public final String customerId;
    public final String itemId;
    public final Instant createdAt = Instant.now();

    public final AtomicReference<State> state = new AtomicReference<>(State.PENDING);
    public final AtomicReference<String> assignedPartnerId = new AtomicReference<>(null);

    // below status can be used for tracking purpose
    public volatile Instant assignedAt, pickedAt, deliveredAt, cancelledAt;

    public Order(String id, String customerId, String itemId) {
        this.id = id;
        this.customerId = customerId;
        this.itemId = itemId;
    }
}



