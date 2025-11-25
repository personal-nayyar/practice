package LLD.machine_hd.vendingMachine;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

enum VendingMachineStatus {
    READY,
    ITEM_SELECTED,
    COLLECTING_CASH,
    DISPENSING_ITEM,
    RETURNING_CHANGE,
    ADD_ITEM,
    CANCELLED,
    SOLD_OUT,
}

@ToString
class Item {
    String name;
    double price;
    int quantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static Builder builder(){
        return new Builder();
    }

    public Item(Builder builder){
        this.name = builder.name;
        this.price = builder.price;
        this.quantity = builder.quantity;
    }


    public static class Builder {
        private String name;
        private double price;
        private int quantity;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Item build(){
            return new Item(this);
        }
    }
}

// Singleton pattern
class InventoryManager {
    // singleton design pattern
    static InventoryManager instance = new InventoryManager();
    public static InventoryManager getInstance(){
        return instance;
    }

    Map<String, Item> inventoryMap;
    private InventoryManager(){
        this.inventoryMap  = new HashMap<>();
    }

    private void initInventory(){
        this.inventoryMap.put("Item1", Item.builder().setName("Item1").setPrice(1.0).setQuantity(10).build());
        this.inventoryMap.put("Item2", Item.builder().setName("Item2").setPrice(2.0).setQuantity(10).build());
        this.inventoryMap.put("Item3", Item.builder().setName("Item3").setPrice(3.0).setQuantity(10).build());
    }
}

class Change {
    Map<Integer, Integer> change; // coin value to quantity
    Change(){
        this.change = new HashMap<>();
    }
}

interface  PaymentStrategy {
    double collect();
    double refund(double amount);
}

class CoinPaymentStrategy implements PaymentStrategy {
    Change change;
    @Override
    public double collect() {
        return change.change.entrySet().stream().mapToDouble(entry -> entry.getKey() * entry.getValue()).sum();
    }

    @Override
    public double refund(double amount) {
        return amount;
    }
}

class CardPaymentStrategy implements PaymentStrategy {
    @Override
    public double collect() {
        return 0.0;
    }

    @Override
    public double refund(double amount) {
        return amount;
    }
}


@AllArgsConstructor
public class VendingMachine {
    VendingMachineStatus status;
    InventoryManager inventoryManager;
}

interface VendingMachineService{
    Item selectItem(String itemName,  int quantity);
    double collectCash(Optional<PaymentStrategy> paymentStrategyOptional, Change change);
    Item dispenseItem(Item item, double amountCollected);
    void cancelTransaction(Item item, double amountCollected);
    void displayInventory();
    VendingMachineStatus getStatus();
    void addItem(String name, int quantity, double price);
}

class VendingMachineImpl implements VendingMachineService {
    static VendingMachineImpl instance;
    // singleton design pattern
    public static VendingMachineImpl getInstance(){
        if (instance == null){
            synchronized (VendingMachineService.class){
                if (instance == null){
                    instance = new VendingMachineImpl();
                }
            }
        }
        return instance;
    }


    VendingMachine vendingMachine;
    ReentrantLock lock;
    PaymentStrategy paymentStrategy;
    VendingMachineImpl(){
        this.vendingMachine = new VendingMachine(VendingMachineStatus.READY, InventoryManager.getInstance());
        this.lock = new ReentrantLock();
        this.paymentStrategy = new CoinPaymentStrategy(); // default payment strategy
    }

    @Override
    public void addItem(String name, int quantity,double price) {
        if (this.vendingMachine.status != VendingMachineStatus.READY) {
            throw new RuntimeException("Vending machine is not ready");
        }
        System.out.println("Adding item: " + name + " quantity: " + quantity + " price: " + price);
        // update quantity if already exist item
        Item item;
        if (this.vendingMachine.inventoryManager.inventoryMap.containsKey(name)){
            item = this.vendingMachine.inventoryManager.inventoryMap.get(name);
            item.quantity += quantity;
        }else {
            item = Item.builder().setName(name).setQuantity(quantity).setPrice(price).build();
        }

        this.vendingMachine.inventoryManager.inventoryMap.put(name, item);
    }


    @Override
    public Item selectItem(String itemName, int quantity) {
        System.out.println("Selecting item: " + itemName + " quantity: " + quantity);
        if (this.vendingMachine.status != VendingMachineStatus.READY) {
            throw new RuntimeException("Vending machine is not ready");
        }
        this.vendingMachine.status = VendingMachineStatus.ITEM_SELECTED;

        // check for item availability
        System.out.println("Item availability: " + this.vendingMachine.inventoryManager.inventoryMap.get(itemName).quantity);
        if (this.vendingMachine.inventoryManager.inventoryMap.get(itemName) == null || this.vendingMachine.inventoryManager.inventoryMap.get(itemName).quantity < quantity) {
            throw new RuntimeException("Item not available");
        }
        System.out.println("Item selected: " + itemName + " quantity: " + quantity);
        return Item.builder().setName(itemName).setQuantity(quantity).setPrice(this.vendingMachine.inventoryManager.inventoryMap.get(itemName).price).build();
    }

    @Override
    public double collectCash(Optional<PaymentStrategy> paymentStrategyOptional, Change change) {
        // check status first
        if (vendingMachine.status != VendingMachineStatus.ITEM_SELECTED)
            throw new RuntimeException("Item not selected");

        System.out.println("Collecting cash");
        this.vendingMachine.status = VendingMachineStatus.COLLECTING_CASH;
        PaymentStrategy paymentStrategy1 = paymentStrategyOptional.orElse(this.paymentStrategy);
        if (paymentStrategy1 instanceof CoinPaymentStrategy){
            System.out.println("Coin payment strategy");
            CoinPaymentStrategy coinPaymentStrategy = (CoinPaymentStrategy) paymentStrategy1;
            coinPaymentStrategy.change = change;
            return coinPaymentStrategy.collect();
        } else {
            System.out.println("Card payment strategy");
            return paymentStrategy1.collect();
        }
    }

    @Override
    public Item dispenseItem(Item item, double amountCollected) {
        // check Machine status
        if (this.vendingMachine.status != VendingMachineStatus.COLLECTING_CASH) {
            throw new RuntimeException("Item not selected or cash not collected");
        }

        // calculate item price
        double itemPrice = item.price * item.quantity;

        // validate collected amount
        System.out.println("Item price: "+ itemPrice + " amount collected: " + amountCollected);
        if (itemPrice > amountCollected)
            throw new RuntimeException("Insufficient payment");

        // dispense item
        System.out.println("Dispensing item");
        this.vendingMachine.status = VendingMachineStatus.DISPENSING_ITEM;
        int availableQuantity = this.vendingMachine.inventoryManager.inventoryMap.get(item.name).quantity;
        availableQuantity -= item.quantity;
        // update inventoryManager
        System.out.println("remaining Available quantity: " + availableQuantity);
        this.vendingMachine.inventoryManager.inventoryMap.put(item.name, Item.builder().setName(item.name).setQuantity(availableQuantity).build());
        System.out.println("updated inventoryManager");
        this.vendingMachine.status = VendingMachineStatus.READY;
        System.out.println("Item dispensed and machine set to reset");
        return item;
    }

    @Override
    public void cancelTransaction(Item item, double amountCollected) {
        // return change
        if (!(vendingMachine.status == VendingMachineStatus.ITEM_SELECTED
                || vendingMachine.status == VendingMachineStatus.COLLECTING_CASH))
            throw new RuntimeException("Item not selected or cash not collected");
        // return change
        System.out.println("Returning change");
        this.paymentStrategy.refund(amountCollected);

        // update status
        this.vendingMachine.status = VendingMachineStatus.READY;
        System.out.println("Machine set to READY");
    }

    @Override
    public void displayInventory() {
        System.out.println("Displaying inventoryManager:");
        this.vendingMachine.inventoryManager.inventoryMap.values().forEach(item -> {
            System.out.println(item);
        });
        System.out.println("==============================================");
    }

    @Override
    public VendingMachineStatus getStatus() {
        return this.vendingMachine.status;
    }
}

class Runner{
    public static void main(String[] args) {
        VendingMachineService vendingMachineService = VendingMachineImpl.getInstance();
        vendingMachineService.addItem("Coke", 10,1 );
        vendingMachineService.addItem("Pepsi", 10, 1);
        vendingMachineService.addItem("Water", 10, 1);
        vendingMachineService.displayInventory();

        Item item = vendingMachineService.selectItem("Coke", 10);
        double amountCollected = vendingMachineService.collectCash(Optional.empty(), new Change());
        vendingMachineService.dispenseItem(item, amountCollected);
        vendingMachineService.displayInventory();

        item = vendingMachineService.selectItem("Water", 1);
        amountCollected = vendingMachineService.collectCash(Optional.empty(), new Change());
        vendingMachineService.cancelTransaction(item, amountCollected);
        vendingMachineService.displayInventory();

        item = vendingMachineService.selectItem("Coke", 1);
        amountCollected = vendingMachineService.collectCash(Optional.empty(), new Change());
        vendingMachineService.dispenseItem(item, amountCollected);
        vendingMachineService.displayInventory();

        item = vendingMachineService.selectItem("Pepsi", 1);
        amountCollected = vendingMachineService.collectCash(Optional.empty(), new Change());
        vendingMachineService.dispenseItem(item, amountCollected);
        vendingMachineService.displayInventory();
    }
}

