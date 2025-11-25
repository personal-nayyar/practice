package design_pattern.behavioral;

public class StateDesignPattern{ }
class OrderStateDemo {

    // --- State Interface ---
    interface State {
        void next(OrderContext ctx);
        void prev(OrderContext ctx);
        void printStatus();
    }

    // --- Context Class ---
    static class OrderContext {
        private State state;

        public OrderContext() {
            state = new NewOrderState(); // initial state
        }

        public void setState(State state) {
            this.state = state;
        }

        public void nextState() {
            state.next(this);
        }

        public void prevState() {
            state.prev(this);
        }

        public void printStatus() {
            state.printStatus();
        }
    }

    // --- Concrete States as Inner Classes ---
    static class NewOrderState implements State {
        public void next(OrderContext ctx) {
            ctx.setState(new ShippedState());
        }

        public void prev(OrderContext ctx) {
            System.out.println("Order is in its initial state.");
        }

        public void printStatus() {
            System.out.println("Order placed, awaiting shipment.");
        }
    }

    static class ShippedState implements State {
        public void next(OrderContext ctx) {
            ctx.setState(new DeliveredState());
        }

        public void prev(OrderContext ctx) {
            ctx.setState(new NewOrderState());
        }

        public void printStatus() {
            System.out.println("Order shipped, on the way to delivery.");
        }
    }

    static class DeliveredState implements State {
        public void next(OrderContext ctx) {
            System.out.println("Order already delivered. No next state.");
        }

        public void prev(OrderContext ctx) {
            ctx.setState(new ShippedState());
        }

        public void printStatus() {
            System.out.println("Order delivered successfully!");
        }
    }

    // --- Driver Code ---
    public static void main(String[] args) {
        OrderContext order = new OrderContext();

        order.printStatus(); // New
        order.nextState();

        order.printStatus(); // Shipped
        order.nextState();

        order.printStatus(); // Delivered
        order.nextState();   // No next state
    }
}