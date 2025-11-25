package design_pattern.creational;

/**
 In Factory pattern, we create factory(interface) for object creation of similar type, hiding the creation logic complexity from client.
 we create object without exposing the creation logic to the client
 and provide a common interface/factory for creating object of same types.
 Imagine that you’re creating a Notification application.
 The first version of your app can only handle email notification, so the bulk of your code lives inside the EmailNotification class.
 After a while, your app becomes pretty popular. Each day you receive dozens of requests for SMS notification from user.
 intent:
    an interface for creating objects of a superclass but allows subclasses to decide the type of objects that will be created.

 examples:
 Consider an example of using multiple database servers like SQL Server, PostgresSQL and Oracle.
 Consider an example of bank operating diff a/c type like saving, current  and in future many more.
 * */

public class Factory{} // just a public class

abstract class Bike{
    abstract void assemble();
}

class ElectricBike extends Bike{
    public ElectricBike() {}

    @Override
    public void assemble() {
        System.out.println("Driving Sedan Car");
    }
}

class SportBike extends Bike{
    public SportBike() {}

    @Override
    public void assemble() {
        System.out.println("Driving Luxury Car");
    }
}

enum BikeType{
    ELECTRIC,
    SPORT
}

class BikeFactory{
    public static Bike buildCar(BikeType bikeType){
        Bike bike = null;
        switch (bikeType){
            case ELECTRIC:
                bike = new ElectricBike();
                break;
            case SPORT:
                bike = new SportBike();
                break;
            default:
                System.out.println("Invalid Car Type");
        }
        return bike;
    }
}
