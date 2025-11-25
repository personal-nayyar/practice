# Flipkart Minutes – Quick Commerce System

## Overview
Flipkart Minutes is an instant-delivery platform that enables customers to order any item and get it delivered within minutes. Your task is to design and implement the core backend system that manages:

- Customer onboarding  
- Delivery partner onboarding  
- Order placement, assignment, and fulfillment  
- Real-time status tracking  
- Concurrency-safe operations  

This system is a standalone, in-memory application (no databases, no APIs).

## Core Requirements

### 1. Onboarding
- System must support onboarding of:
  - Customers  
  - Delivery partners  
- Each onboarded entity should have a unique ID and name.

### 2. Order Placement & Cancellation
- Customers can place an order for any item.
- All items are considered always available — no inventory checks.
- Customers may cancel an order only if it is not yet picked up by a delivery partner.
- Once picked up, the order cannot be canceled.

### 3. Order Assignment & Fulfillment
- Orders are auto‑assigned to any available delivery partner.
- If no partner is available, orders enter a queue.
- Delivery partners:
  - Can handle only one order at a time.
  - Can pick up assigned orders.
  - Can mark orders as delivered.
- Canceled orders must not be assigned.
- If an assigned order is canceled before pickup, the partner becomes available again.
- Delivery partners are available 24×7.
- Ignore travel time.

### 4. Status Tracking
System must provide real‑time status for:
- Orders  
- Delivery partners  

Order statuses:
- CREATED  
- ASSIGNED  
- PICKED_UP  
- DELIVERED  
- CANCELED  

Partner statuses:
- AVAILABLE  
- BUSY  

### 5. Concurrency & Thread Safety
- System must be thread‑safe.
- Multiple customers/partners can act simultaneously.

## Bonus Features (Optional)

### Notifications  
Simulate logs/prints to notify customers and delivery partners of status changes.

### Ratings  
Customers can rate delivery partners after delivery.

### Dashboard  
Show top delivery partners based on:
- Number of deliveries  
- Ratings  

### Auto‑cancel  
If an order is not picked up within 30 minutes:
- Auto‑cancel it, whether assigned or not.

## Guidelines
- Time limit: 120 minutes.
- Implementation must be modular, clean, readable, and extensible.
- Use design patterns where applicable.
- Only in‑memory data structures allowed.
- Provide a driver program / test class.
- Handle errors and edge cases gracefully.
- No UI or HTTP APIs.
- AI tools like ChatGPT or Copilot are strictly prohibited.
- Reasonable assumptions may be made.

## Sample Commands

```
onboard_customer <customer_id> <name>
onboard_delivery_partner <partner_id> <name>

create_order <customer_id> <item_name>
cancel_order <order_id>

show_order_status <order_id>
show_partner_status <partner_id>

pick_up_order <partner_id> <order_id>
complete_order <partner_id> <order_id>
```

## Sample Expected Behavior
- When an order is created and a partner is available, it gets auto‑assigned.
- If no partner is available, the order waits in a queue.
- When a partner becomes free, the next order is assigned.
- Cancelled orders must never be reassigned.
- After pickup, orders cannot be canceled.

## Assumptions
- All IDs provided are unique.
- All items exist (no catalog checks).
- Orders are queued FIFO unless implemented differently.

## Deliverables
- Complete runnable source code.
- Driver program with multiple tests.
- Clean and modular design.
