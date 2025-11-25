package LLD.machine_hd.trafficSignal;


import lombok.Getter;
import lombok.Setter;
import utils.ThreadUtils;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

enum LightColor {
    RED,
    GREEN,
    YELLOW
}

enum Direction{
    NORTH,
    SOUTH,
    EAST,
    WEST
}

@Getter
@Setter
class TrafficLight {
    private Direction direction;
    private LightColor color;
    public TrafficLight(Direction direction, LightColor color) {
        this.direction = direction;
        this.color = color;
    }
}

interface TimingStrategy{
    public static final int DEFAULT_GREEN_TIME = 10;
    public static final int DEFAULT_YELLOW_TIME = 2;
    public static final int DEFAULT_RED_TIME = 5;
    default int getAllRedTime(){
        return 1;
    }

    int getGreenTime(Direction direction, Map<Direction, Integer> vehicleCount);
    int getYellowTime();
    int getRedTime();
}

class DefaultTimingStrategy implements TimingStrategy{
    @Override
    public int getGreenTime(Direction direction, Map<Direction, Integer> vehicleCount){
        return DEFAULT_GREEN_TIME;
    }

    @Override
    public int getYellowTime(){
        return DEFAULT_YELLOW_TIME;
    }

    @Override
    public int getRedTime(){
        return DEFAULT_RED_TIME;
    }
}

class AdaptiveTimingStrategy implements TimingStrategy{
    @Override
    public int getGreenTime(Direction direction, Map<Direction, Integer> vehicleCount){
        return DEFAULT_GREEN_TIME+ vehicleCount.get(direction);
    }

    @Override
    public int getYellowTime(){
        return DEFAULT_YELLOW_TIME;
    }

    @Override
    public int getRedTime(){
        return DEFAULT_RED_TIME;
    }
}

class VehicleSensor{
    Map<Direction, Integer> vehicleCount = new HashMap<>(){{
        put(Direction.EAST, 0);
        put(Direction.WEST, 0);
        put(Direction.NORTH, 0);
        put(Direction.SOUTH, 0);
    }};
    void detectVehicle(Direction direction, int count){
        vehicleCount.put(direction, vehicleCount.get(direction) + count);
    }

    void resetVehicleCount(Set<Direction> directions){
        for (Direction direction: directions){
            vehicleCount.put(direction, 0);
        }
    }
}
interface TrafficSignalController{
    void updateTrafficLight(Set<Direction> directions, LightColor color);
}


class TrafficSignal implements TrafficSignalController{
    Map<Direction, LightColor> trafficLights;
    VehicleSensor vehicleSensor;
    TimingStrategy timingStrategy;

    ExecutorService scheduler = Executors.newSingleThreadExecutor();

    // Defines the order of greens - simple two-phase: NS and EW
    private final List<Set<Direction>> phases = Arrays.asList(
            EnumSet.of(Direction.NORTH, Direction.SOUTH),
            EnumSet.of(Direction.EAST, Direction.WEST)
    );

    public TrafficSignal(TimingStrategy timingStrategy) {
        this.timingStrategy = timingStrategy;
        this.vehicleSensor = new VehicleSensor();
        this.trafficLights = new HashMap<>(){{
            put(Direction.EAST, LightColor.RED);
            put(Direction.WEST, LightColor.RED);
            put(Direction.NORTH, LightColor.RED);
            put(Direction.SOUTH, LightColor.RED);
        }};
    }

    @Override
    public void updateTrafficLight(Set<Direction> directions, LightColor color) {
        // update the traffic lights smoothly for given direction and all other directions to RED
        for (Direction direction: Direction.values()){
            if (directions.contains(direction)){
                trafficLights.put(direction, color);
            }
            else{
                trafficLights.put(direction, LightColor.RED);
            }
        }
        printTrafficLights();
    }

    private int getTime(Set<Direction> directions,LightColor color){
        switch(color){
            case RED:
                return timingStrategy.getRedTime();
            case YELLOW:
                return timingStrategy.getYellowTime();
            case GREEN:
                return computeGreenTime(directions);
            default:
                return 0;
        }
    }

    private int computeGreenTime(Set<Direction> directions){
        int greenTime = 0;
        for(Direction direction: directions){
            greenTime = Math.max(greenTime, timingStrategy.getGreenTime(direction, vehicleSensor.vehicleCount));
        }
        return greenTime;
    }


    private void printTrafficLights(){
        for(Direction direction: trafficLights.keySet()){
            System.out.println(direction + ": " + trafficLights.get(direction));
        }
        System.out.println("----------------\n");
    }

    private void  sleepSeconds(int seconds){
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start(){
        SignalHandlerThread signalHandlerThread = new SignalHandlerThread(this);
        signalHandlerThread.start();
    }

    class SignalHandlerThread extends Thread{
        TrafficSignal trafficSignal;
        public SignalHandlerThread(TrafficSignal trafficSignal){
            this.trafficSignal = trafficSignal;
        }

        @Override
        public void run(){
            int phaseIndex = 0;
            // Mark all lights as red
            updateTrafficLight(trafficLights.keySet(), LightColor.RED);
            sleepSeconds(timingStrategy.getAllRedTime());
            while(true){


                // get the current phase
                Set<Direction> currentPhase = phases.get(phaseIndex);

                // get the green time
                int greenTime = getTime(currentPhase, LightColor.GREEN);
                updateTrafficLight(currentPhase, LightColor.GREEN);
                TimeCounter timeCounter = new TimeCounter(greenTime);
                timeCounter.start();
                try {
                    timeCounter.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // get the yellow time
                int yellowTime = getTime(currentPhase, LightColor.YELLOW);
                updateTrafficLight(currentPhase, LightColor.YELLOW);
                timeCounter = new TimeCounter(yellowTime);
                timeCounter.start();
                try {
                    timeCounter.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                vehicleSensor.resetVehicleCount(phases.get(phaseIndex));

                // change the phase
                phaseIndex = (phaseIndex + 1) % phases.size();
            }
        }
    }

    static class TimeCounter extends  Thread{
        int requiredSecond;
        public TimeCounter(int requiredSecond){
            this.requiredSecond = requiredSecond;
        }

        @Override
        public void run(){
            int time = 1;
            while(time <= requiredSecond){
                System.out.print(" "+time++);
                ThreadUtils.sleepSeconds(1);
            }
            System.out.println("\n");
        }
    }

    private void handleEmergencyVehicle(Direction direction){
        updateTrafficLight(EnumSet.of(direction), LightColor.GREEN);
        sleepSeconds(5);
    }
}


class TrafficSignalTest{
    public static void main(String[] args) {
        TrafficSignal trafficSignal = new TrafficSignal(new AdaptiveTimingStrategy());
        trafficSignal.start();
        ThreadUtils.sleepSeconds(10);
    }
}









