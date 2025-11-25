package LLD.machine_hd.coffeeMachine_unravel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import utils.DSAUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Bevarage{

}

enum BeverageType {
    CAPPUCCINO,
    LATTE,
    ESPRESSO,
}

// Singleton Pattern
class InventoryManager {
    Map<String, Integer> inventory;
    private static final InventoryManager instance = new InventoryManager();
    private InventoryManager() {
        initializeInventory();
    }

    private void initializeInventory() {
        this.inventory = new HashMap<>(){{
            put("Milk", 100);
            put("Coffee", 50);
            put("Sugar", 100);
            put("Water", 100);
        }};
    }

    public static InventoryManager getInstance() {
        return instance;
    }

    public void addIngredient(String ingredient, int quantity) {
        this.inventory.put(ingredient, this.inventory.getOrDefault(ingredient, 0) + quantity);
    }

    public void removeIngredient(String ingredient, int quantity) {
        this.inventory.put(ingredient, this.inventory.getOrDefault(ingredient, 0) - quantity);
    }

    public int getIngredient(String ingredient) {
        return this.inventory.get(ingredient);
    }

    public Map<String, Integer> getInventory() {
        return this.inventory;
    }
}

@Setter
@Getter
@AllArgsConstructor
abstract class Beverage {
    protected String name;
    protected Map<String, Integer> recipe;
    protected IStrategy brewStrategy;

    public final String brew(InventoryManager inventoryManager) {
        // check if ingredients are available
        checkIngredients(inventoryManager);

        // customPrepare beverage
        brewStrategy.customPrepare();

        // deduct ingredients
        deductIngredients(inventoryManager);

        return "Your "+this.name+" is ready. Enjoy!";
    }

    private void checkIngredients(InventoryManager inventoryManager) {
        this.getRecipe().forEach((ingredient, quantity) -> {
            if (inventoryManager.inventory.get(ingredient) < quantity) {
                throw new RuntimeException("Ingredient " + ingredient + " is not available");
            }
        });
    }

    public void deductIngredients(InventoryManager inventoryManager) {
        this.getRecipe().forEach((ingredient, quantity) -> {
            inventoryManager.inventory.put(ingredient, inventoryManager.inventory.get(ingredient) - quantity);
        });
    }

    abstract void customPrepare();
    abstract Map<String, Integer> getRecipe();
}

class Cappuccino extends Beverage {
    public Cappuccino() {
        super("Cappuccino", Map.of("Milk", 100, "Coffee", 50), new CappuccinoStrategy());
    }

    @Override
    public void customPrepare() {
        System.out.println("Preparing " + this.name);
    }

    @Override
    public Map<String, Integer> getRecipe() {
        return this.recipe;
    }
}

class Latte extends Beverage {
    public Latte() {
        super("Latte", Map.of("Milk", 120, "Coffee", 50), new LatteStrategy());
    }
    @Override
    public void customPrepare() {
        System.out.println("Preparing " + this.name);
    }

    @Override
    public Map<String, Integer> getRecipe() {
        return this.recipe;
    }
}

class Espresso extends Beverage {
    public Espresso() {
        super("Espresso", Map.of("Milk", 120, "Coffee", 50), new EspressoStrategy());
    }
    @Override
    public void customPrepare() {
        System.out.println("Preparing " + this.name);
    }

    @Override
    public Map<String, Integer> getRecipe() {
        return this.recipe;
    }
}

// Factory for Beverage
class BeverageFactory {
    public static Beverage createBeverage(String type){
        switch (type){
            case "CAPPUCCINO":
                return new Cappuccino();
            case "LATTE":
                return new Latte();
            case "ESPRESSO":
                    return new Espresso();
            default:
                throw new RuntimeException("Beverage type not supported");
        }
    }
}

interface ICoffeeMachine {
    void addIngredient(String ingredient, int quantity);
    void removeIngredient(String ingredient, int quantity);
    void addBeverage(Beverage beverage);
    void removeBeverage(Beverage beverage);
    String brewBeverage(String type);
}

class CoffeeMachineImpl implements ICoffeeMachine{
    InventoryManager inventoryManager;
    List<Beverage> beverages;

    public CoffeeMachineImpl(InventoryManager inventoryManager, List<Beverage> beverages) {
        this.inventoryManager = inventoryManager;
        this.beverages = beverages;
    }

    @Override
    public void addIngredient(String ingredient, int quantity) {
        this.inventoryManager.inventory.put(ingredient, this.inventoryManager.inventory.get(ingredient) + quantity);
    }

    @Override
    public void removeIngredient(String ingredient, int quantity) {
        this.inventoryManager.inventory.put(ingredient, this.inventoryManager.inventory.get(ingredient) - quantity);
    }

    @Override
    public void addBeverage(Beverage beverage) {
        beverages.stream().filter(b -> b.getName().equals(beverage.getName())).findAny().ifPresent(b -> {
            throw new RuntimeException("Beverage already exists");
        });
        this.beverages.add(beverage);
    }

    @Override
    public void removeBeverage(Beverage beverage) {
        beverages.stream().filter(b -> b.getName().equals(beverage.getName())).findAny()
                .orElseThrow(() -> new RuntimeException("Beverage does not exist"));

        beverages.remove(beverage);
    }

    @Override
    public String brewBeverage(String type) {
        // if beverage not exists throw exception
        System.out.println("Brewing " + type);
        System.out.println("Available Beverages: ");
        beverages.stream().map(Beverage::getName).forEach(System.out::println);
        beverages.stream().filter(b -> b.getName().equalsIgnoreCase(type)).findAny().orElseThrow(
                () -> new RuntimeException("Beverage does not exist")
        );
        Beverage beverage = beverages.stream().filter(b -> b.getName().equalsIgnoreCase(type)).findAny().get();
        beverage.brew(this.inventoryManager);
        return "";
    }
}

class Runner{
    public static void main(String[] args) {
        InventoryManager inventoryManager = InventoryManager.getInstance();
        inventoryManager.addIngredient("Milk", 100);
        inventoryManager.addIngredient("Coffee", 50);
        DSAUtils.printMap(inventoryManager.inventory);

        List<Beverage> beverages = new ArrayList<>(){{
            add(BeverageFactory.createBeverage("CAPPUCCINO"));
            add(BeverageFactory.createBeverage("LATTE"));
            add(BeverageFactory.createBeverage("ESPRESSO"));
        }};
        System.out.println("Available Beverages:");
        beverages.stream().map(Beverage::getName).forEach(System.out::println);


        ICoffeeMachine coffeeMachine = new CoffeeMachineImpl(inventoryManager, beverages);
        coffeeMachine.brewBeverage("CAPPUCCINO");

        DSAUtils.printMap(inventoryManager.inventory);
    }
}

// strategy pattern
interface IStrategy {
    void customPrepare();
}

class CappuccinoStrategy implements IStrategy {
    @Override
    public void customPrepare() {
        System.out.println("Preparing Cappuccino");
    }
}

class LatteStrategy implements IStrategy {
    @Override
    public void customPrepare() {
        System.out.println("Preparing Latte");
    }
}

class EspressoStrategy implements IStrategy {
    @Override
    public void customPrepare() {
        System.out.println("Preparing Espresso");
    }
}
