package companies.wayfair;

import utils.DSAUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

interface IOrder{
    void setName(String name);
    String getName();
    void setPrice(int price);
    int getPrice();
}

class Order implements IOrder{
    String name;
    int price;

    Order(){}

    public Order(String name, int price){
        this.name = name;
        this.price = price;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public int getPrice() {
        return price;
    }

    public int hashCode(){
        return name.hashCode();
    }

    public boolean equals(Object obj){
        if(obj instanceof Order){
            Order other = (Order) obj;
            return this.name.equals(other.name);
        }
        return false;
    }

    public String toString(){
        return name + " : " + price;
    }
}

public interface IOrderManagement {
    void addToCart(IOrder order);
    void removeFromCart(IOrder order); // remove all quantity of this order
    int calculatePrice();
    Map<String, Double> discountCategory();
    Map<String, Integer> cartItems();
}

class OrderManagement implements IOrderManagement{
    Map<Order, Integer> cart;
    Map<String, Integer> discount;

    OrderManagement(){
        cart = new HashMap<>();
        discount = new HashMap<>(){{
           put("cheap", 10);
           put("medium", 20);
           put("expensive", 30);
        }};
    }

    @Override
    public void addToCart(IOrder order) {
//        System.out.println(order.hashCode());
        cart.put((Order)order, cart.getOrDefault(order, 0) + 1);
    }

    @Override
    public void removeFromCart(IOrder order) {
        cart.remove(order);
    }

    @Override
    public int calculatePrice() {
        printCart();
        int price = 0;
        for(Map.Entry<Order, Integer> entry : cart.entrySet()){
            price += entry.getKey().getPrice() * entry.getValue();
        }
        return price;
    }

    @Override
    public Map<String, Double> discountCategory() {
        Map<String, Double> discountCategory = new HashMap<>();
        for(Map.Entry<Order, Integer> entry : cart.entrySet()){
            int price = entry.getKey().getPrice();
            if(price <= 10){
                discountCategory.put("cheap", discountCategory.getOrDefault("cheap", 0.0) + price * entry.getValue());
            } else if(price <= 20){
                discountCategory.put("medium", discountCategory.getOrDefault("medium", 0.0) + price * entry.getValue());
            } else {
                discountCategory.put("expensive", discountCategory.getOrDefault("expensive", 0.0) + price * entry.getValue());
            }
        }
        discountCategory.forEach((key, value) -> discountCategory.put(key, value * discount.get(key)/100));
        return discountCategory;
    }

    @Override
    public Map<String, Integer> cartItems() {
        return cart.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().getName(), entry -> entry.getValue()));
    }

    public void printCart(){
        System.out.println("Cart: ");
        cart.forEach((key, value) -> System.out.println(key.getName() + " : " + value));
    }
}

class Runner{
    public static void main(String[] args) {
        OrderManagement orderManagement = new OrderManagement();

        orderManagement.addToCart(new Order("order-1", 10));
        orderManagement.addToCart(new Order("order-1", 10));
        orderManagement.addToCart(new Order("order-3", 30));

        System.out.println("Expected: 60, Actual: " + orderManagement.calculatePrice());
        System.out.println("Expected: {cheap=2, expensive=9}, Actual: " + orderManagement.discountCategory());
        System.out.println("Expected: {order-1=1, order-2=1, order-3=1}, Actual: " + orderManagement.cartItems());

        OrderManagement orderManagement2 = new OrderManagement();
        orderManagement2.addToCart(new Order("order-1", 5));
        orderManagement2.addToCart(new Order("order-2", 15));
        orderManagement2.addToCart(new Order("order-3", 30));

        System.out.println("Expected: 50, Actual: " + orderManagement2.calculatePrice());
        System.out.println("Expected: {cheap=0.5, medium=3.0, expensive=9.0}, Actual: " + orderManagement2.discountCategory());
        System.out.println("Expected: {order-1=1, order-2=1, order-3=1}, Actual: " + orderManagement2.cartItems());

        OrderManagement orderManagement3 = new OrderManagement();
        orderManagement3.addToCart(new Order("order-1", 3));
        orderManagement3.addToCart(new Order("order-2", 6));
        orderManagement3.addToCart(new Order("order-3", 9));

        System.out.println("Expected: 18, Actual: " + orderManagement3.calculatePrice());
        System.out.println("Expected: {cheap=1.8,}, Actual: " + orderManagement3.discountCategory());
        System.out.println("Expected: {order-1=1, order-2=1, order-3=1}, Actual: " + orderManagement3.cartItems());

        OrderManagement orderManagement4 = new OrderManagement();
        for(int i = 0; i < 10; i++){
            orderManagement4.addToCart(new Order("order-"+ThreadLocalRandom.current().nextInt(1, 11), ThreadLocalRandom.current().nextInt(1, 30)));
        }
        System.out.println("Expected: 5050, Actual: " + orderManagement4.calculatePrice());
        System.out.println("Expected: {cheap=4950, medium=10}, Actual: " + orderManagement4.discountCategory());
        System.out.println("Expected: {order-0=2, order-1=2, order-2=2, ...}, Actual: " + orderManagement4.cartItems());
    }
}

class OrderManagementService implements IOrderManagement{
    Map<String, Integer> discount =  new HashMap<>(){{
       put("cheap", 10);
       put("medium", 20);
       put("expensive", 30);
    }};
    Map<String, List<IOrder>> cart = new HashMap<>();


    @Override
    public void addToCart(IOrder order) {
        cart.computeIfAbsent(order.getName(),  k -> new ArrayList<>()).add(order);
    }

    @Override
    public void removeFromCart(IOrder order) {
        cart.remove(order.getName());
    }

    @Override
    public int calculatePrice() {
        int[] totalAmount = new int[2];
//        cart.forEach((key, value) -> {
//            value.forEach(order -> {
//                totalAmount[0] += order.getPrice();
//            });
//        });

//        cart.values().forEach(list -> {
//            list.forEach(order -> {
//                totalAmount[0] += order.getPrice();
//            });
//        });

//        List<IOrder> orders = cart.values().stream().flatMap(new Function<List<IOrder>, Stream<IOrder>>() {
//            @Override
//            public Stream<IOrder> apply(List<IOrder> iOrders) {
//                return iOrders.stream();
//            }
//        }).collect(Collectors.toUnmodifiableList());
//        orders.forEach(order -> totalAmount[0] += order.getPrice());

        cart.values().stream().flatMap(list -> list.stream())
                .forEach(order -> totalAmount[0] += order.getPrice());

        return totalAmount[0];
    }

    @Override
    public Map<String, Double> discountCategory() {
        Map<String, Double> discountCategory = new HashMap<>();
        cart.values().stream().flatMap(list -> list.stream())
                .forEach(order -> {
                    int price = order.getPrice();
                    if(price <= 10){
                        discountCategory.put("cheap", discountCategory.getOrDefault("cheap", 0.0) + price * discount.get("cheap")/100);
                    } else if(price <= 20){
                        discountCategory.put("medium", discountCategory.getOrDefault("medium", 0.0) + price * discount.get("medium")/100);
                    } else {
                        discountCategory.put("expensive", discountCategory.getOrDefault("expensive", 0.0) + price * discount.get("expensive")/100);
                    }
                });
        return discountCategory;
    }

    @Override
    public Map<String, Integer> cartItems() {
        return cart.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().size()));
    }

    public static void main(String[] args) {
        OrderManagementService orderManagement = new OrderManagementService();

        orderManagement.addToCart(new Order("order-1", 10));
        orderManagement.addToCart(new Order("order-1", 10));
        orderManagement.addToCart(new Order("order-3", 30));

        System.out.println("Expected: 60, Actual: " + orderManagement.calculatePrice());
        System.out.println("Expected: {cheap=2, expensive=9}, Actual: " + orderManagement.discountCategory());
        System.out.println("Expected: {order-1=1, order-2=1, order-3=1}, Actual: " + orderManagement.cartItems());

        OrderManagementService orderManagement2 = new OrderManagementService();
        orderManagement2.addToCart(new Order("order-1", 5));
        orderManagement2.addToCart(new Order("order-2", 15));
        orderManagement2.addToCart(new Order("order-3", 30));

        System.out.println("Expected: 50, Actual: " + orderManagement2.calculatePrice());
        System.out.println("Expected: {cheap=0.5, medium=3.0, expensive=9.0}, Actual: " + orderManagement2.discountCategory());
        System.out.println("Expected: {order-1=1, order-2=1, order-3=1}, Actual: " + orderManagement2.cartItems());

        OrderManagementService orderManagement3 = new OrderManagementService();
        orderManagement3.addToCart(new Order("order-1", 3));
        orderManagement3.addToCart(new Order("order-2", 6));
        orderManagement3.addToCart(new Order("order-3", 9));

        System.out.println("Expected: 18, Actual: " + orderManagement3.calculatePrice());
        System.out.println("Expected: {cheap=1.8,}, Actual: " + orderManagement3.discountCategory());
        System.out.println("Expected: {order-1=1, order-2=1, order-3=1}, Actual: " + orderManagement3.cartItems());

        OrderManagementService orderManagement4 = new OrderManagementService();
        for(int i = 0; i < 10; i++){
            orderManagement4.addToCart(new Order("order-"+ThreadLocalRandom.current().nextInt(1, 11), ThreadLocalRandom.current().nextInt(1, 30)));
        }
        System.out.println("Expected: 5050, Actual: " + orderManagement4.calculatePrice());
        System.out.println("Expected: {cheap=4950, medium=10}, Actual: " + orderManagement4.discountCategory());
        System.out.println("Expected: {order-0=2, order-1=2, order-2=2, ...}, Actual: " + orderManagement4.cartItems());
    }
}

