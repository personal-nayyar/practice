package A_interview_experiences.flipkart.flipkartminutes.schedulars;

import A_interview_experiences.flipkart.flipkartminutes.Notification.NotificationService;
import A_interview_experiences.flipkart.flipkartminutes.model.Order;
import A_interview_experiences.flipkart.flipkartminutes.repository.OrderRepository;
import A_interview_experiences.flipkart.flipkartminutes.service.OrderService;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OrderAssignmentScheduler {
    private final OrderService orderService;
    private final OrderRepository orderRepo;
    private final NotificationService notificationService;

    // Two independent scheduled executors
    private final ScheduledExecutorService assignmentScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService cancellationScheduler = Executors.newSingleThreadScheduledExecutor();

    /** Threshold beyond which pending orders are auto-cancelled. */
    private static final Duration AUTO_CANCEL_THRESHOLD = Duration.ofMinutes(30);

    public OrderAssignmentScheduler(OrderService orderService,
                                    OrderRepository orderRepo,
                                    NotificationService notificationService) {
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.notificationService = notificationService;
    }

    // Starts both background schedulers.
    public void start() {
        startAssignmentScheduler();
        startAutoCancelScheduler();
        System.out.println("[SCHEDULER] Both order assignment and auto-cancel schedulers started...");
    }

    // Gracefully stops both schedulers.
    public void stop() {
        stopExecutor(assignmentScheduler, "Assignment Scheduler");
        stopExecutor(cancellationScheduler, "Auto-Cancel Scheduler");
    }

    private void startAssignmentScheduler() {
        Runnable assignTask = () -> {
            try {
                int pending = orderService.getPendingOrdersCount();
                int available = orderService.getFreePartnerCount();

                if (pending == 0 || available == 0) return;

                System.out.printf("[%s] [ASSIGNER] Checking: pending=%d, freePartners=%d%n",
                        Instant.now(), pending, available);

                List<String> assignedOrders = new ArrayList<>();
                while (orderService.hasPendingOrder() && orderService.hasFreePartner()) {
                    String orderId = orderService.pollPendingOrder();
                    Order o = orderRepo.findById(orderId).orElse(null);
                    if (o == null || o.state.get() != Order.State.PENDING) continue;
                    orderService.assignIfPossible(o);
                    assignedOrders.add(orderId);
                }

                if (!assignedOrders.isEmpty()) {
                    System.out.println("[ASSIGNER] Assigned orders: " + assignedOrders);
                }
            } catch (Exception e) {
                System.err.println("[ASSIGNER] Error: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Run every 1 minute
        assignmentScheduler.scheduleAtFixedRate(assignTask, 0, 1, TimeUnit.MINUTES);
        System.out.println("[ASSIGNER] Scheduler created (every 1 minute)");
    }

    private void startAutoCancelScheduler() {
        Runnable cancelTask = () -> {
            try {
                Instant now = Instant.now();
                List<String> cancelledOrders = new ArrayList<>();

                for (Order o : orderRepo.findAll()) {
                    if (o.state.get() == Order.State.PENDING) {
                        Duration age = Duration.between(o.createdAt, now);
                        if (age.compareTo(AUTO_CANCEL_THRESHOLD) > 0) {
                            o.state.set(Order.State.AUTO_CANCELLED);
                            orderRepo.save(o);
                            orderService.removeFromPendingQueue(o.id);
                            cancelledOrders.add(o.id);
                            notificationService.notifyAsync(
                                    o.customerId,
                                    "Order " + o.id + " auto-cancelled (pending > 30 mins)"
                            );
                        }
                    }
                }

                if (!cancelledOrders.isEmpty()) {
                    System.out.println("[CANCELLER] Auto-cancelled orders: " + cancelledOrders);
                }

            } catch (Exception e) {
                System.err.println("[CANCELLER] Error: " + e.getMessage());
                e.printStackTrace();
            }
        };

        // Run every 5 minutes
        cancellationScheduler.scheduleAtFixedRate(cancelTask, 0, 5, TimeUnit.MINUTES);
        System.out.println("[CANCELLER] Scheduler crated (every 5 minutes)");
    }

    private void stopExecutor(ScheduledExecutorService executor, String name) {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            System.out.println("[SCHEDULER] " + name + " stopped successfully.");
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}