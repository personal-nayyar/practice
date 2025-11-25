package design_pattern.structural;

/**
 * Decorator Pattern Demo: Car Customization Example
 * 
 * This example shows how to use the Decorator pattern to add features to a basic car
 * dynamically at runtime without affecting other car instances.
 */
public class Decorator {
    public static void main(String[] args) {
        // Basic car
        Car basicCar = new BasicCar();
        System.out.println("Basic Car: " + basicCar.getDescription() + "; Cost: $" + basicCar.getCost());
        
        // Car with Sports Package
        Car sportsCar = new SportsPackage(new BasicCar());
        System.out.println("\nSports Car: " + sportsCar.getDescription() + "; Cost: $" + sportsCar.getCost());
        
        // Car with Luxury Package
        Car luxuryCar = new LuxuryPackage(new BasicCar());
        System.out.println("\nLuxury Car: " + luxuryCar.getDescription() + "; Cost: $" + luxuryCar.getCost());
        
        // Car with both Sports and Luxury Packages
        Car sportsLuxuryCar = new SportsPackage(new LuxuryPackage(new BasicCar()));
        System.out.println("\nSports-Luxury Car: " + sportsLuxuryCar.getDescription() + "; Cost: $" + sportsLuxuryCar.getCost());
        
        // Car with all features
        Car fullyLoadedCar = new PremiumSoundSystem(
                                new Sunroof(
                                    new SportsPackage(
                                        new LuxuryPackage(
                                            new BasicCar()))));
        System.out.println("\nFully Loaded Car: " + fullyLoadedCar.getDescription() + "; Cost: $" + fullyLoadedCar.getCost());
    }
}

// Component Interface
interface Car {
    String getDescription();
    double getCost();
}

// Concrete Component
class BasicCar implements Car {
    @Override
    public String getDescription() {
        return "Basic Car";
    }
    
    @Override
    public double getCost() {
        return 20000.0; // Base price of a basic car
    }
}

// Abstract Decorator
abstract class CarDecorator implements Car {
    protected Car decoratedCar;
    
    public CarDecorator(Car car) {
        this.decoratedCar = car;
    }
    
    @Override
    public String getDescription() {
        return decoratedCar.getDescription();
    }
    
    @Override
    public double getCost() {
        return decoratedCar.getCost();
    }

    abstract void additionalFeature();
}

// Concrete Decorators
class SportsPackage extends CarDecorator {
    public SportsPackage(Car car) {
        super(car);
        additionalFeature();
    }

    public void additionalFeature() {
        System.out.println("Adding sport features: sport suspension, alloy wheels, racing stripes");
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + ", Sports Package (sport suspension, alloy wheels, racing stripes)";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 5000.0;
    }
}

class LuxuryPackage extends CarDecorator {
    public LuxuryPackage(Car car) {
        super(car);
        additionalFeature();
    }

    public void additionalFeature() {
        System.out.println("Adding luxury features: leather seats, wood trim, ambient lighting");
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + ", Luxury Package (leather seats, wood trim, ambient lighting)";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 8000.0;
    }
}

class Sunroof extends CarDecorator {
    public Sunroof(Car car) {
        super(car);
        additionalFeature();
    }

    public void additionalFeature() {
        System.out.println("Adding sunroof");
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + ", Sunroof";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 1500.0;
    }
}

class PremiumSoundSystem extends CarDecorator {
    public PremiumSoundSystem(Car car) {
        super(car);
    }

    public void additionalFeature() {
        System.out.println("Adding premium sound system");
    }
    
    @Override
    public String getDescription() {
        return super.getDescription() + ", Premium Sound System";
    }
    
    @Override
    public double getCost() {
        return super.getCost() + 1200.0;
    }
}
