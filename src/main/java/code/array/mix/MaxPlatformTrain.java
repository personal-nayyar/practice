package code.array.mix;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Given arrival and departure times of all trains that reach a railway station.
 * Your task is to find the minimum number of platforms required for the railway station so that no train waits.
 * <p>
 *     Input:  n = 6
 *     arr[] = {0900, 0940, 0950, 1100, 1500, 1800}
 *     dep[] = {0910, 1200, 1120, 1130, 1900, 2000}
 *     Output: 3
 *     Explanation: Minimum 3 platforms are required to safely arrive and depart all trains.
 */
public class MaxPlatformTrain {
    public static void main(String[] args) {
        int[] arr = {900, 940, 950, 1100, 1500, 1800};
        int[] dep = {910, 1200, 1120, 1130, 1900, 2000};
        System.out.println(maxPlatform(arr, dep));
    }

    static int maxPlatform(int[] arr, int[] dep) {
        Arrays.sort(arr);
        Arrays.sort(dep);
        int i = 0; // represents arrival of train
        int j = 0; // represents departure of train
        int platform = 0;
        int maxPlatform = 0;
        if(arr[i] < dep[j]){ // if new train arrival is before departure of previous train
            platform++;
            maxPlatform =  Math.max(maxPlatform, platform);
            i++; // consider next train arrival
        }else{
            platform--;
            j++; // consider next train departure
        }
        while (i < arr.length && j < dep.length) {
            if (arr[i] < dep[j]) {
                platform++;
                maxPlatform =  Math.max(maxPlatform, platform);
                i++;
            } else {
                platform--;
                j++;
            }
        }
        return maxPlatform;
    }
}

@Data
class TrainEvent implements Comparable<TrainEvent> {
    int time;
    boolean isArrival;

    public TrainEvent(int time, boolean isArrival) {
        this.time = time;
        this.isArrival = isArrival;
    }

    @Override
    public int compareTo(TrainEvent other) {
        // Sort by time, and arrivals come before departures if times are equal
        return Integer.compare(time, other.time);
    }
}
class MaxTrainsInInterval {

    public static void main(String[] args) {
        int[] arr = {900, 940, 950, 1100, 1500, 1800};
        int[] dep = {910, 1200, 1120, 1130, 1900, 2000};

        List<TrainEvent> trainEvents = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            trainEvents.add(new TrainEvent(arr[i], true));
            trainEvents.add(new TrainEvent(dep[i], false));
        }

        int[] result = findMaxTrainsInInterval(trainEvents);
        System.out.println("Maximum trains present in any interval: " + result[0]);
        System.out.println("Time interval: [" + result[1] + ", " + result[2] + "]");
    }

    private static int[] findMaxTrainsInInterval(List<TrainEvent> trainEvents) {
        Collections.sort(trainEvents);

        int currentTrains = 0;
        int maxTrains = 0;
        int startTime = -1;
        int endTime = Integer.MAX_VALUE;

        for (TrainEvent event : trainEvents) {
            if (event.isArrival) {
                currentTrains++;
                if (currentTrains > maxTrains) {
                    maxTrains = currentTrains;
                    startTime = event.time;
                }
            } else {
                currentTrains--;
                if (currentTrains == maxTrains-1) {
                    endTime = event.time;
                }
            }
        }
        return new int[]{maxTrains, startTime, endTime};
    }
}
