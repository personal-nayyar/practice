package code.array.divive_n_conquer.BinarySearch;

import java.util.Arrays;

/*
Koko loves to eat bananas. There are n piles of bananas, the ith pile has piles[i] bananas. The guards have gone and will come back in h hours.
Koko can decide her bananas-per-hour eating speed of k. Each hour, she chooses some pile of bananas and eats k bananas from that pile.
If the pile has less than k bananas, she eats all of them instead and will not eat any more bananas during this hour.
Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.
Return the minimum integer k such that she can eat all the bananas within h hours.

Example 1:
Input: piles = [3,6,7,11], h = 8
Output: 4

Example 2:
Input: piles = [30,11,23,4,20], h = 5
Output: 30

Example 3:
Input: piles = [30,11,23,4,20], h = 6
Output: 23

🧠 Problem Understanding

Koko can eat at a speed of k bananas/hour.
She needs to finish all piles within h hours.

Each hour she:
	•	Eats up to k bananas from one pile.
	•	If the pile has fewer than k bananas, she eats them all and stops for that hour.

We must find the minimum k (integer speed) such that she can finish all piles within h hours.

⸻

💡 Approach — Binary Search

The minimum speed (k) can be between:
	•	1 banana/hour (slowest)
	•	max(piles) bananas/hour (fastest — eats one full pile per hour)

We can use binary search to find the smallest k such that she finishes within h hours.

Steps:
	1.	Set search space → low = 1, high = max(piles).
	2.	For a middle value mid (candidate eating speed):
	•	Compute total hours needed if Koko eats at mid speed.
	•	If hours ≤ h, she can eat slower → move left (high = mid).
	•	Else, move right (low = mid + 1).
	3.	The first valid mid where total hours ≤ h is our answer.

* */
public class MinSpeedToEatAllBanana {
    // Function to calculate minimum speed K
    public static int minEatingSpeed(int[] piles, int h) {
        int left = 1;
        int right = Arrays.stream(piles).max().getAsInt(); // max pile = upper bound

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canEatAll(piles, mid, h)) {
                right = mid; // try smaller speed
            } else {
                left = mid + 1; // increase speed
            }
        }

        return left; // minimum valid speed
    }

    // Helper function: check if Koko can finish all piles at speed = k within h hours
    private static boolean canEatAll(int[] piles, int k, int h) {
        int hours = 0;
        for (int pile : piles) {
            // hours = ceil(pile / k)
            hours += Math.ceil((double) pile / k);
        }
        return hours <= h;
    }

    public static void main(String[] args) {
        int[] piles1 = {3, 6, 7, 11};
        int h1 = 8;
        System.out.println("Output: " + minEatingSpeed(piles1, h1)); // 4

        int[] piles2 = {30, 11, 23, 4, 20};
        int h2 = 5;
        System.out.println("Output: " + minEatingSpeed(piles2, h2)); // 30

        int[] piles3 = {30, 11, 23, 4, 20};
        int h3 = 6;
        System.out.println("Output: " + minEatingSpeed(piles3, h3)); // 23
    }
}
