package code.array.divive_n_conquer.BinarySearch;

//https://leetcode.com/problems/capacity-to-ship-packages-within-d-days/description/

import java.util.Arrays;

/**
 A conveyor belt has packages that must be shipped from one port to another within days days.

 The ith package on the conveyor belt has a weight of weights[i]. Each day, we load the ship with packages on the conveyor belt (in the order given by weights). We may not load more weight than the maximum weight capacity of the ship.

 Return the least weight capacity of the ship that will result in all the packages on the conveyor belt being shipped within days days.
 Example 1:

 Input: weights = [1,2,3,4,5,6,7,8,9,10], days = 5
 Output: 15
 Explanation: A ship capacity of 15 is the minimum to ship all the packages in 5 days like this:
 1st day: 1, 2, 3, 4, 5
 2nd day: 6, 7
 3rd day: 8
 4th day: 9
 5th day: 10

 Note that the cargo must be shipped in the order given, so using a ship of capacity 14 and splitting the packages into parts like (2, 3, 4, 5), (1, 6, 7), (8), (9), (10) is not allowed.
 */
public class minCapacityToShip {
    public static void main(String[] args) {
        int[] weights = {1,2,3,4,5,6,7,8,9,10};
        int days = 5;
        System.out.println(minCapacityToShip(weights, days));
    }

    static int minCapacityToShip(int[] weights, int days){
        int min = Arrays.stream(weights).max().getAsInt();
        int max = Arrays.stream(weights).sum(); // max weight = upper bound
        while (min < max){
            int mid = min + (max - min) / 2;
            if (canShip(weights, mid, days)){
                max = mid;
            }else{
                min = mid + 1;
            }
        }
        return min;
    }

    static boolean canShip(int[] weights, int capacity, int days){
        int currentCapacity = 0;
        int daysTaken = 1;
        for (int weight : weights){
            if (weight > capacity){
                return false;
            }
            if (currentCapacity + weight > capacity){
                daysTaken++;
                currentCapacity = 0;
            }
            currentCapacity += weight;
        }
        return daysTaken <= days;
    }
}
