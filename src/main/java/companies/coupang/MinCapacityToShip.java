package companies.coupang;

import java.util.Arrays;

public class MinCapacityToShip {
    public static void main(String[] args) {
        int[] weights = {1,2,3,4,5,6,7,8,9,10};
        int days = 5;
        System.out.println(minCapacityToShip(weights, days));
    }

    static int minCapacityToShip(int[] weights, int days){
        int left = 1;
        int right = Arrays.stream(weights).max().getAsInt(); // max weight = upper bound
        while (left < right){
            int mid = left + (right - left) / 2;
            if (canShip(weights, mid, days)){
                right = mid;
            }else{
                left = mid + 1;
            }
        }
        return left;
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
