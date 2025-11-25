package LLD.parkinglot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

enum VehicleType {
    BIKE,
    TRUCK,
    CAR
}
// OCP
abstract class Vehicle{
    String licenseNumber;
    VehicleType type;
    int spotsNeeded;

    public Vehicle(String licenseNumber){
        this.licenseNumber = licenseNumber;
    }

    abstract int getSpotsNeeded();
}

class Car extends Vehicle{
    public Car(String licenseNumber){
        super(licenseNumber);
        this.type = VehicleType.CAR;
        this.spotsNeeded = 4;
    }

    @Override
    public int getSpotsNeeded() {
        return this.spotsNeeded;
    }
}

class Bike extends Vehicle{
    public Bike(String licenseNumber){
        super(licenseNumber);
        this.type = VehicleType.BIKE;
        this.spotsNeeded = 2;
    }

    @Override
    public int getSpotsNeeded() {
        return this.spotsNeeded;
    }
}

class Truck extends Vehicle{
    public Truck(String licenseNumber){
        super(licenseNumber);
        this.type = VehicleType.TRUCK;
        this.spotsNeeded = 6;
    }

    @Override
    public int getSpotsNeeded() {
        return this.spotsNeeded;
    }
}

// factory design pattern
class VehicleFactory{
    public Vehicle getVehicle(VehicleType vehicleType, String licenseNumber) {
        if (vehicleType == null) {
            return null;
        }
        switch (vehicleType) {
            case CAR:
                return new Car(licenseNumber);
            case BIKE:
                return new Bike(licenseNumber);
            case TRUCK:
                return new Truck(licenseNumber);
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
    }
}

class Floor{
    int ROWS = 3;
    int COLUMNS = 10;
    int floorNumber;
    int[][] parkingSpots;
    Floor(int floorNumber){
        this.floorNumber = floorNumber;
        this.parkingSpots = new int[ROWS][COLUMNS];
    }
}

class ParkingSpot{
    int floorNumber;
    int row;
    int column;
    boolean isAvailable;
}

// Builder design pattern
class ParkingTicket{
    int ticketNumber;
    String vehicleNumber;
    int floorNumber;
    ParkingSpot parkingSpot;
    int parkingFee;
    boolean isPaid;
    long entryTime;
    long exitTime;

    ParkingTicket(Builder builder){
        this.ticketNumber = builder.ticketNumber;
        this.vehicleNumber = builder.vehicleNumber;
        this.floorNumber = builder.floorNumber;
        this.parkingSpot = builder.parkingSpot;
    }

    static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private int ticketNumber;
        private String vehicleNumber;
        private int floorNumber;
        private ParkingSpot parkingSpot;
        private long entryTime;
        private long exitTime;
        private double parkingFee;

        public Builder  ticketNumber(int ticketNumber){
            this.ticketNumber = ticketNumber;
            return this;
        }

        public Builder vehicleNumber(String vehicleNumber){
            this.vehicleNumber = vehicleNumber;
            return this;
        }

        public Builder floorNumber(int floorNumber){
            this.floorNumber = floorNumber;
            return this;
        }

        public Builder parkingSpot(ParkingSpot parkingSpot){
            this.parkingSpot = parkingSpot;
            return this;
        }

        public Builder entryTime(Long entryTime){
            this.entryTime = entryTime;
            return this;
        }

        public Builder exitTime(Long exitTime){
            this.exitTime = exitTime;
            return this;
        }

        public Builder parkingFee(int parkingFee){
            this.parkingFee = parkingFee;
            return this;
        }

        public ParkingTicket build(){
            return new ParkingTicket(this);
        }
    }
}

public class ParkingLot{
    int totalFloors;
    List<Floor> floors;
    ParkingLot(int totalFloors){
        this.totalFloors = totalFloors;
        this.floors = new ArrayList<>();
        for(int i = 0; i < totalFloors; i++){
            this.floors.add(new Floor(i));
        }
    }
}

// Strategy pattern
interface PaymentStrategy{
    void pay(int amount);
}

class CreditCardPaymentStrategy implements PaymentStrategy{
    public void pay(int amount){
        System.out.println("Paid " + amount + " using credit card");
    }
}

class CashPaymentStrategy implements PaymentStrategy{
    public void pay(int amount){
        System.out.println("Paid " + amount + " using cash");
    }
}

interface ParkingStrategy{
    ParkingSpot assingnSpot(VehicleType vehicletype);
}

class FirstAvailableParkingStrategy implements ParkingStrategy{
    ParkingLot parkingLot;
    FirstAvailableParkingStrategy(ParkingLot parkingLot){
        this.parkingLot = parkingLot;
    }

    public ParkingSpot findAvailableSpot(VehicleType vehicletype){
        for(int i = 0; i < this.parkingLot.totalFloors; i++){
            for(int j = 0; j < this.parkingLot.floors.get(i).ROWS; j++){
                for(int k = 0; k < this.parkingLot.floors.get(i).COLUMNS; k++){
                    if(this.parkingLot.floors.get(i).parkingSpots[j][k] == 0){
                        ParkingSpot parkingSpot = new ParkingSpot();
                        parkingSpot.row = j;
                        parkingSpot.column = k;
                        parkingSpot.isAvailable = true;
                        return parkingSpot;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public ParkingSpot assingnSpot(VehicleType vehicletype) {
        return findAvailableSpot(vehicletype);
    }
}

// Strategy pattern
class ParkingSpotManualAssignmentStrategy implements ParkingStrategy{
    ParkingLot parkingLot;
    ParkingSpot parkingSpot;
    ParkingSpotManualAssignmentStrategy(ParkingLot parkingLot, ParkingSpot parkingSpot){
        this.parkingLot = parkingLot;
        this.parkingSpot = parkingSpot;
    }
    @Override
    public ParkingSpot assingnSpot(VehicleType vehicletype) {
        return null;
    }
}

interface ParkingLotService{
    ParkingTicket parkVehicle(String licenseNumber, VehicleType vehicleType, ParkingStrategy parkingStrategy);
    void leaveVehicle(ParkingTicket ticket);
    void pay(int amount, PaymentStrategy paymentStrategy);
    void displayAvailableSpots();
}

interface ParkingFeeStrategy{
    int calculateFee(ParkingTicket ticket);
}

class FlatFeeStrategy implements ParkingFeeStrategy{
    int fee;
    FlatFeeStrategy(int fee){
        this.fee = fee;
    }
    @Override
    public int calculateFee(ParkingTicket ticket){
        return this.fee;
    }
}

class TimeBasedFeeStrategy implements ParkingFeeStrategy{
    int feePerHour;
    TimeBasedFeeStrategy(int feePerHour){
        this.feePerHour = feePerHour;
    }
    @Override
    public int calculateFee(ParkingTicket ticket){
        long timeDiff = System.currentTimeMillis() - ticket.entryTime;
        long hours = timeDiff / (60 * 60 * 1000);
        return (int) (hours * this.feePerHour);
    }
}

// Singleton Pattern
class ParkingLotServiceImpl implements ParkingLotService{
    // implement singleton design pattern
    private static ParkingLotServiceImpl instance;
    private ParkingLotServiceImpl(){}

    public static ParkingLotServiceImpl getInstance(){
        if (instance == null){
            synchronized (ParkingLotServiceImpl.class){
                if(instance == null){
                    instance = new ParkingLotServiceImpl();
                    instance.parkingLot = new ParkingLot(3);
                    instance.vehicleFactory = new VehicleFactory();
                    instance.parkingStrategy = new FirstAvailableParkingStrategy(instance.parkingLot);
                    instance.parkingFeeStrategy = new TimeBasedFeeStrategy(10);
                }
            }
        }
        return instance;
    }


    ParkingLot parkingLot;
    VehicleFactory vehicleFactory;
    ParkingFeeStrategy parkingFeeStrategy;
    ParkingStrategy parkingStrategy;

//    public ParkingLotServiceImpl(){
//        this.parkingLot = new ParkingLot(3);
//        this.vehicleFactory = new VehicleFactory();
//        this.parkingFeeStrategy = new TimeBasedFeeStrategy(10);
//    }

    @Override
    public ParkingTicket parkVehicle(String licenseNumber, VehicleType vehicleType, ParkingStrategy parkingStrategy){
        Vehicle vehicle = vehicleFactory.getVehicle(vehicleType, licenseNumber);
        ParkingSpot parkingSpot = parkingStrategy.assingnSpot(vehicleType);
        if(parkingSpot == null){
            throw new RuntimeException("Parking spot not available");
        }
        // Mark parking spot as occupied
        markParkingSpotAsOccupied(parkingSpot, vehicle);
        System.out.println("Vehicle parked successfully at floor " + parkingSpot.floorNumber + " row " + parkingSpot.row + " column " + parkingSpot.column);

        // generate Ticket
        ParkingTicket ticket = generateTicket(parkingSpot, vehicle);
        return ticket;
    }

    @Override
    public void leaveVehicle(ParkingTicket ticket) {
        // release parking spot
        markParkingSpotAsUnoccupied(ticket.parkingSpot);
        System.out.println("Vehicle left successfully at floor " + ticket.parkingSpot.floorNumber + " row " + ticket.parkingSpot.row + " column " + ticket.parkingSpot.column);
        // calculate parking fee
        ticket.parkingFee = parkingFeeStrategy.calculateFee(ticket);
        // make payment
        pay(ticket.parkingFee, new CashPaymentStrategy());

        // update ticket status
        ticket.isPaid = true;
    }

    @Override
    public void pay(int amount, PaymentStrategy paymentStrategy) {
        paymentStrategy.pay(amount);
    }

    @Override
    public void displayAvailableSpots() {
        for(int i = 0; i < this.parkingLot.totalFloors; i++){
            Floor floor = this.parkingLot.floors.get(i);
            System.out.println("Floor " + (i+1) + ": ");
            for(int j = 0; j < floor.ROWS; j++){
                for(int k = 0; k < floor.COLUMNS; k++){
                    if(floor.parkingSpots[j][k] == 0){
                        System.out.print("O ");
                    }else{
                        System.out.print(floor.parkingSpots[j][k]+" ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private synchronized boolean markParkingSpotAsOccupied(ParkingSpot parkingSpot, Vehicle vehicle) {
        Floor floor = parkingLot.floors.get(parkingSpot.floorNumber);
        if (floor.parkingSpots[parkingSpot.row][parkingSpot.column] == 0) { // unoccupied
            floor.parkingSpots[parkingSpot.row][parkingSpot.column] = vehicle.spotsNeeded;
            return true;
        }
        return false;
    }

    private synchronized boolean markParkingSpotAsUnoccupied(ParkingSpot parkingSpot) {
        Floor floor = parkingLot.floors.get(parkingSpot.floorNumber);
        floor.parkingSpots[parkingSpot.row][parkingSpot.column] = 0;
        return true;
    }

    private ParkingTicket generateTicket(ParkingSpot parkingSpot, Vehicle vehicle){
        return ParkingTicket.builder()
                .ticketNumber(ThreadLocalRandom.current().nextInt(1, 1000))
                .vehicleNumber(vehicle.licenseNumber)
                .floorNumber(parkingSpot.floorNumber)
                .parkingSpot(parkingSpot)
                .entryTime(System.currentTimeMillis())
                .build();
    }

    private double calculateParkingFee(ParkingTicket ticket){
        long timeDiff = System.currentTimeMillis() - ticket.entryTime;
        ticket.parkingFee = (int) (timeDiff * 0.1);
        ticket.exitTime = System.currentTimeMillis();
        return ticket.parkingFee;
    }
}

class Runner{
    public static void main(String[] args) {
        ParkingLotService parkingLotService = ParkingLotServiceImpl.getInstance();
        parkingLotService.displayAvailableSpots();

        ParkingStrategy parkingStrategy = ((ParkingLotServiceImpl) parkingLotService).parkingStrategy;
        ParkingTicket ticket1 = parkingLotService.parkVehicle("KA-01-HH-1234", VehicleType.CAR, parkingStrategy);
        ParkingTicket ticket2 = parkingLotService.parkVehicle("KA-01-HH-1235", VehicleType.BIKE, parkingStrategy);
        ParkingTicket ticket3 = parkingLotService.parkVehicle("KA-01-HH-1236", VehicleType.TRUCK, parkingStrategy);
        parkingLotService.displayAvailableSpots();
        parkingLotService.leaveVehicle(ticket2);
        parkingLotService.displayAvailableSpots();

        ParkingStrategy parkingStrategy2 = new ParkingSpotManualAssignmentStrategy(((ParkingLotServiceImpl) parkingLotService).parkingLot, ticket1.parkingSpot);
        parkingLotService.parkVehicle("KA-01-HH-1237", VehicleType.CAR, parkingStrategy2);
        parkingLotService.displayAvailableSpots();
    }
}





