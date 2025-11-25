package LLD.machine_hd.ElevatorSystem;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

enum ElevatorStatus{
    IDLE,
    MOVING_UP,
    MOVING_DOWN,
    DOOR_OPEN,
    DOOR_CLOSED,
    NOT_IN_SERVICE;
}

enum Direction {
    UP,
    DOWN,
    NONE
}

@ToString
abstract class BasicRequest{ // Encapsulation, Abstraction
    int sourceFloor; // null for external request i.e. pickup request
    int destinationFloor;
    Direction direction; // null for internal request i.e. drop request
}

// external request
class PickupRequest extends BasicRequest{
    PickupRequest(int floor, Direction direction){
        this.destinationFloor = floor;
        this.direction = direction;
    }
}

class DropRequest extends BasicRequest{
    DropRequest(int sourceFloor, int destinationFloor){
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.direction = sourceFloor < destinationFloor ? Direction.UP : Direction.DOWN;
    }
}

// Strategy Pattern
interface ElevatorProcessor{
    void processRequest(Elevator elevator);
}

// first come first serve
class FIFOProcessor implements ElevatorProcessor{
    @Override
    public void processRequest(Elevator elevator){
        BasicRequest request = elevator.requests.remove(0);
        elevator.processRequest(request);
    }
}

class MinDistanceProcessor implements ElevatorProcessor{
    @Override
    public void processRequest(Elevator elevator){
        // nearest to the current floor of elevator
        BasicRequest request = elevator.requests.stream().min((r1, r2) -> Math.abs(r1.destinationFloor - elevator.currentFloor) - Math.abs(r2.destinationFloor - elevator.currentFloor)).get();
        elevator.requests.remove(request);
        elevator.processRequest(request);
    }
}

class PriorityProcessor implements ElevatorProcessor{
    @Override
    public void processRequest(Elevator elevator){
        // nearest to the current floor of elevator in the same direction of FIFOProcessor
        Direction direction = elevator.requests.get(0).direction;
        System.out.println("Direction:"+direction);
        BasicRequest request = elevator.requests.stream().filter(r -> r.direction == direction).min((r1, r2) -> Math.abs(r1.destinationFloor - elevator.currentFloor) - Math.abs(r2.destinationFloor - elevator.currentFloor)).get();
        elevator.requests.remove(request);
        elevator.processRequest(request);
    }
}


@Setter
@Getter
@AllArgsConstructor
abstract class Elevator{ // SRP, OCP
    int id;
    int currentFloor;
    ElevatorStatus status;
    List<BasicRequest> requests;

    abstract void openDoors();
    abstract void closeDoors();
    abstract void moveToFloor(int destinationFloor);
    abstract void processRequest(BasicRequest request);
    abstract void addRequest(BasicRequest request);
}

class DefaultElevator extends Elevator{
    private ElevatorProcessor processor;
    DefaultElevator(int id){
        super(id, 0, ElevatorStatus.IDLE, new ArrayList<>());
        processor = new PriorityProcessor();
        startRequestProcessor();
    }

    @Override
    public void openDoors() {
        System.out.println("Elevator " + this.getId() +" at floor: "+ this.currentFloor +  ": Opening doors (arrived).");
        this.setStatus(ElevatorStatus.DOOR_OPEN);
    }

    @Override
    void closeDoors() {
        System.out.println("Elevator " + this.getId() +" at floor: "+ this.currentFloor +  ": Closing doors (arrived).");
        this.setStatus(ElevatorStatus.DOOR_CLOSED);
    }

    @Override
    void moveToFloor(int destinationFloor) {
        System.out.println("Elevator " + this.getId() + ": Moving to floor " + destinationFloor);
        this.setStatus(this.currentFloor < destinationFloor ? ElevatorStatus.MOVING_UP : ElevatorStatus.MOVING_DOWN);
        this.currentFloor = destinationFloor;
        openDoors();
        closeDoors();
    }

    @Override
    void processRequest(BasicRequest request) {
        if(request instanceof PickupRequest){
            moveToFloor(request.destinationFloor);
        }
        if(request instanceof DropRequest){
            moveToFloor(request.destinationFloor);
        }
    }

    @Override
    void addRequest(BasicRequest request){
        this.requests.add(request);
    }


    private Thread requestProcessorThread;

    private void startRequestProcessor() {
        if (requestProcessorThread == null) {
            requestProcessorThread = new Thread(() -> {
                while (true) {
                    if (!requests.isEmpty()) {
                        processor.processRequest(this);
                    } else {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            requestProcessorThread.start();
        }
    }
}

class Building{ // SRP
    int totalFloors;
    List<Elevator> elevators;
    Building(int totalFloors){
        this.totalFloors = totalFloors;
        this.elevators = new ArrayList<>(){{
            add(new DefaultElevator(1));
            add(new DefaultElevator(2));
            add(new DefaultElevator(3));
        }};
    }
}


// Strategy Pattern
interface SchedularStrategy{
    void scheduleRequest(List<Elevator> elevators, BasicRequest request);
}

class NearestSchedularStrategy implements SchedularStrategy{
    @Override
    public void scheduleRequest(List<Elevator> elevators, BasicRequest request){
        Direction direction = request.direction;
        // filter elevators based on direction
        elevators = elevators.stream().filter(elevator -> elevator.status == ElevatorStatus.IDLE || elevator.status == ElevatorStatus.MOVING_UP).toList();

        // get the nearest elevator in the filtered list
        int nearestElevator = elevators.get(0).currentFloor;
        int minDistance = Math.abs(elevators.get(0).currentFloor - request.destinationFloor);
        for(Elevator elevator: elevators) {
            int distance = Math.abs(elevator.currentFloor - request.destinationFloor);
            if(distance < minDistance) {
                minDistance = distance;
                nearestElevator = elevator.currentFloor;
            }
        }
        for(Elevator elevator: elevators) {
            if(elevator.currentFloor == nearestElevator) {
                System.out.println("Elevator " + elevator.id + " is assigned to request " + request);
                elevator.addRequest(request);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
        }
    }
}


class ElevatorSystem{ // Factory Pattern
    Building building;
    SchedularStrategy schedularStrategy;
    ElevatorSystem(){
        building = new Building(10);
        this.schedularStrategy = new NearestSchedularStrategy();
    }


    public void addRequest(int floor, Direction direction){
        BasicRequest request = new PickupRequest(floor, direction);
        this.schedularStrategy.scheduleRequest(this.building.elevators, request);
    }

    public void addRequest(int sourceFloor, int destinationFloor, Elevator elevator){
        BasicRequest request = new DropRequest(sourceFloor, destinationFloor);
        elevator.addRequest(request);
    }
}

class Runner{
    public static void main(String[] args) {
        ElevatorSystem elevatorSystem = new ElevatorSystem();
        elevatorSystem.addRequest(5, Direction.UP);
        elevatorSystem.addRequest(2, Direction.UP);
        elevatorSystem.addRequest(7, Direction.UP);

        elevatorSystem.addRequest(2, 5, elevatorSystem.building.elevators.get(0));
        elevatorSystem.addRequest(7, 1, elevatorSystem.building.elevators.get(1));
    }
}


