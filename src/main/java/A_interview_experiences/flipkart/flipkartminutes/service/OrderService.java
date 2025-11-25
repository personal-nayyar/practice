package A_interview_experiences.flipkart.flipkartminutes.service;


import A_interview_experiences.flipkart.flipkartminutes.Notification.NotificationService;
import A_interview_experiences.flipkart.flipkartminutes.model.DeliveryPartner;
import A_interview_experiences.flipkart.flipkartminutes.model.Order;
import A_interview_experiences.flipkart.flipkartminutes.repository.CustomerRepository;
import A_interview_experiences.flipkart.flipkartminutes.repository.DeliveryPartnerRepository;
import A_interview_experiences.flipkart.flipkartminutes.repository.OrderRepository;

import java.util.*;
import java.util.concurrent.*;

public class OrderService {

    private final OrderRepository orderRepo;
    private final DeliveryPartnerRepository partnerRepo;
    private final CustomerRepository customerRepo;
    private final NotificationService notificationService;

    private final Queue<String> freePartners = new ConcurrentLinkedQueue<>();
    private final BlockingQueue<String> pendingOrders = new LinkedBlockingQueue<>();

    private final Map<String, Object> orderLocks = new ConcurrentHashMap<>();
    private Object getLockForOrder(String orderId) {
        return orderLocks.computeIfAbsent(orderId, k -> new Object());
    }

    public OrderService(OrderRepository orderRepo, DeliveryPartnerRepository partnerRepo,
                        CustomerRepository customerRepo, NotificationService notif) {
        this.orderRepo = orderRepo;
        this.partnerRepo = partnerRepo;
        this.customerRepo = customerRepo;
        this.notificationService = notif;
    }

    public void registerFreePartner(String partnerId) {
        freePartners.add(partnerId);
    }

    public String placeOrder(String customerId, String itemId) {
        if (!customerRepo.existsById(customerId))
            throw new IllegalArgumentException("Unknown customer: " + customerId);

        Order o = new Order(UUID.randomUUID().toString(), customerId, itemId);
        orderRepo.save(o);
        assignIfPossible(o);
        return o.id;
    }

    public void assignIfPossible(Order order) {
        Object lock  = getLockForOrder(order.getId());
        synchronized (lock) {
            // no two thread can process the same order at the same time
            String partnerId = freePartners.poll();
            if (partnerId == null) {
                pendingOrders.add(order.id);
                return;
            }
            DeliveryPartner p = partnerRepo.findById(partnerId).orElseThrow();
            if (p.getState().get() == DeliveryPartner.State.FREE) {
                p.getState().set(DeliveryPartner.State.ASSIGNED);
                p.getCurrentOrderId().set(order.id);

                order.assignedPartnerId.set(partnerId);
                order.state.set(Order.State.ASSIGNED);

                orderRepo.save(order);
                partnerRepo.save(p);

                notificationService.notifyAsync(partnerId, "Assigned new order: " + order.id);
                notificationService.notifyAsync(order.customerId, "Order assigned to " + partnerId);
            } else {
                pendingOrders.add(order.id);
            }
        }
    }

    public boolean cancelOrder(String customerId, String orderId) {
        var oOpt = orderRepo.findById(orderId);
        if (oOpt.isEmpty()) return false;
        Order o = oOpt.get();
        if (!o.customerId.equals(customerId)) return false;


        if (o.state.get() != Order.State.PENDING && o.state.get() != Order.State.ASSIGNED) {
            throw new IllegalStateException("Cannot cancel order in state " + o.state.get() + ": " + orderId);
        }

        if (o.state.compareAndSet(Order.State.PENDING, Order.State.CANCELLED)) {
            pendingOrders.remove(orderId);
            orderRepo.save(o);
            notificationService.notifyAsync(customerId, "Order cancelled: " + orderId);
            return true;
        }

        if (o.state.compareAndSet(Order.State.ASSIGNED, Order.State.CANCELLED)) {
            DeliveryPartner p = partnerRepo.findById(o.assignedPartnerId.get()).orElse(null);
            if (p != null) {
                p.getState().set(DeliveryPartner.State.FREE);
                p.getCurrentOrderId().set(null);
                freePartners.add(p.getId());
                partnerRepo.save(p);
            }
            orderRepo.save(o);
            notificationService.notifyAsync(customerId, "Cancelled after assignment: " + orderId);
            return true;
        }
        return false;
    }

    public boolean partnerPickup(String partnerId) {
        DeliveryPartner p = partnerRepo.findById(partnerId).orElse(null);
        if (p == null || p.getCurrentOrderId() == null) return false;
        Order o = orderRepo.findById(p.getCurrentOrderId().get()).orElse(null);
        if (o == null) return false;
        if (o.state.compareAndSet(Order.State.ASSIGNED, Order.State.PICKED_UP)) {
            p.getState().set(DeliveryPartner.State.BUSY);

            partnerRepo.save(p);
            orderRepo.save(o);

            notificationService.notifyAsync(o.customerId, "Order picked up!");
            return true;
        }
        return false;
    }

    public boolean partnerDeliver(String partnerId, int rating) {
        DeliveryPartner p = partnerRepo.findById(partnerId).orElse(null);
        if (p == null || p.getCurrentOrderId() == null) return false;
        Order o = orderRepo.findById(p.getCurrentOrderId().get()).orElse(null);
        if (o == null) return false;

        if (o.state.compareAndSet(Order.State.PICKED_UP, Order.State.DELIVERED)) {
            p.getState().set(DeliveryPartner.State.FREE);
            p.getCurrentOrderId().set(null);
            p.addRating(rating);

            freePartners.add(partnerId);
            partnerRepo.save(p);
            orderRepo.save(o);

            notificationService.notifyAsync(o.customerId, "Order delivered successfully!");
            return true;
        }
        return false;
    }

    public String showOrderStatus(String orderId) {
        return orderRepo.findById(orderId)
                .map(o -> String.format("Order[%s] - %s", o.id, o.state.get()))
                .orElse("Order not found");
    }

    public String showPartnerStatus(String partnerId) {
        return partnerRepo.findById(partnerId)
                .map(p -> String.format("Partner[%s] - %s (currentOrder=%s)",
                        p.getId(), p.getState(),
                        p.getCurrentOrderId() == null ? "None" : p.getCurrentOrderId()))
                .orElse("Partner not found");
    }

    public int getPendingOrdersCount() { return pendingOrders.size(); }
    public int getFreePartnerCount() { return freePartners.size(); }
    public boolean hasPendingOrder() { return !pendingOrders.isEmpty(); }
    public boolean hasFreePartner() { return !freePartners.isEmpty(); }
    public String pollPendingOrder() { return pendingOrders.poll(); }
    public void removeFromPendingQueue(String orderId) {
        pendingOrders.remove(orderId);
    }
}