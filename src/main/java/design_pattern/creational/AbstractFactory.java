package design_pattern.creational;

/**
 provide one more layer of abstraction layer to factory design.
 Abstract Factory patterns work around a super-factory which creates other factories.
 This factory is also called as factory of factories.
 * */
public class AbstractFactory {}// just the public class

interface Car {
    void assemble();
}

class MiniCar implements Car {
    @Override
    public void assemble() {
        System.out.println("Assemble Mini Car");
    }
}

class SedanCar implements Car {
    @Override
    public void assemble() {
        System.out.println("Assemble Sedan Car");
    }
}

class SUVCar implements Car {
    @Override
    public void assemble() {
        System.out.println("Assemble SUV Car");
    }
}

enum CarType {
    MINI,
    SEDAN,
    SUV
}

enum BRAND {
    MARUTI,
    HONDA,
    HYUNDAI
}

interface AbstractCarFactory {
    Car buildCar(CarType carType);
}

class HondaCarFactory implements AbstractCarFactory {
    @Override
    public Car buildCar(CarType carType) {
        Car car = null;
        switch (carType) {
            case MINI:
                car = new MiniCar();
                break;
            case SEDAN:
                car = new SedanCar();
                break;
            case SUV:
                car = new SUVCar();
                break;
        }
        return car;
    }
}

class HyundaiCarFactory implements AbstractCarFactory {
    @Override
    public Car buildCar(CarType carType) {
        Car car = null;
        switch (carType) {
            case MINI:
                car = new MiniCar();
                break;
            case SEDAN:
                car = new SedanCar();
                break;
            case SUV:
                car = new SUVCar();
                break;
        }
        return car;
    }
}

class MarutiCarFactory implements AbstractCarFactory {
    @Override
    public Car buildCar(CarType carType) {
        Car car = null;
        switch (carType) {
            case MINI:
                car = new MiniCar();
                break;
            case SEDAN:
                car = new SedanCar();
                break;
            case SUV:
                car = new SUVCar();
                break;
        }
        return car;
    }
}

class CarFactory {
    public static Car buildCar(BRAND brand, CarType carType) {
        AbstractCarFactory carFactory = null;
        Car car = null;
        switch (brand) {
            case MARUTI:
                carFactory = new MarutiCarFactory();
                car = carFactory.buildCar(carType);
                break;
            case HONDA:
                carFactory = new HondaCarFactory();
                car = carFactory.buildCar(carType);
                break;
            case HYUNDAI:
                carFactory = new HyundaiCarFactory();
                car = carFactory.buildCar(carType);
                break;
        }
        return car;
    }
}

class Client {
    public static void main(String[] args) {
        // Create a Maruti Sedan
        Car marutiSedan = CarFactory.buildCar(BRAND.MARUTI, CarType.SEDAN);
        marutiSedan.assemble();

        // Create a Honda SUV
        Car hondaSuv = CarFactory.buildCar(BRAND.HONDA, CarType.SUV);
        hondaSuv.assemble();

        // Create a Hyundai Mini
        Car hyundaiMini = CarFactory.buildCar(BRAND.HYUNDAI, CarType.MINI);
        hyundaiMini.assemble();
    }
}










