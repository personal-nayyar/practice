package A_interview_experiences.flipkart.flipkartminutes.model;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Getter
@Setter
public class DeliveryPartner extends User {

    public enum State { FREE, ASSIGNED, BUSY }

    private final AtomicReference<State> state = new AtomicReference<>(State.FREE);
    private final AtomicReference<String> currentOrderId = new AtomicReference<>(null);
    private final AtomicInteger deliveriesCompleted = new AtomicInteger(0);
    private final AtomicInteger ratingSum = new AtomicInteger(0);
    private final AtomicInteger ratingCount = new AtomicInteger(0);

    public DeliveryPartner(String id, String name) {
        super(id, name);
    }

    public boolean isFree() {
        return state.get() == State.FREE;
    }

    public double getAverageRating() {
        int count = ratingCount.get();
        return count == 0 ? 0.0 : (double) ratingSum.get() / count;
    }

    public AtomicReference<State> getState() { return state; }
    public AtomicReference<String> getCurrentOrderId() { return currentOrderId; }
    public AtomicInteger getDeliveriesCompleted() { return deliveriesCompleted; }

    @Override
    public String toString() {
        return String.format("Partner{id=%s, name=%s, state=%s, deliveries=%d, rating=%.2f}",
                id, name, state.get(), deliveriesCompleted.get(), getAverageRating());
    }

    public void addRating(int rating) {
        this.ratingSum.addAndGet(rating);
        this.ratingCount.incrementAndGet();
    }

    public double getCumulativeRating() {
        return (double) ratingSum.get() / ratingCount.get();
    }
}