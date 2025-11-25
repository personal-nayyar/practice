package code.dp;

import java.util.HashSet;
import java.util.Set;

/*
You have planned some train traveling one year in advance. The days of the year in which you will travel are given as an integer array days. Each day is an integer from 1 to 365.
Train tickets are sold in three different ways:
a 1-day pass is sold for costs[0] dollars,
a 7-day pass is sold for costs[1] dollars, and
a 30-day pass is sold for costs[2] dollars.
The passes allow that many days of consecutive travel.

For example, if we get a 7-day pass on day 2, then we can travel for 7 days: 2, 3, 4, 5, 6, 7, and 8.
Return the minimum number of dollars you need to travel every day in the given list of days.

Example 1:
Input: days = [1,4,6,7,8,20], costs = [2,7,15]
Output: 11
Explanation: For example, here is one way to buy passes that lets you travel your travel plan:
On day 1, you bought a 1-day pass for costs[0] = $2, which covered day 1.
On day 3, you bought a 7-day pass for costs[1] = $7, which covered days 3, 4, ..., 9.
On day 20, you bought a 1-day pass for costs[0] = $2, which covered day 20.
In total, you spent $11 and covered all the days of your travel.

Example 2:
Input: days = [1,2,3,4,5,6,7,8,9,10,30,31], costs = [2,7,15]
Output: 17
Explanation: For example, here is one way to buy passes that lets you travel your travel plan:
On day 1, you bought a 30-day pass for costs[2] = $15 which covered days 1, 2, ..., 30.
On day 31, you bought a 1-day pass for costs[0] = $2 which covered day 31.
In total, you spent $17 and covered all the days of your travel.

* */
public class MinTravelCost {

    public static int mincostTickets(int[] days, int[] costs) {
        // Create a set for faster lookup of travel days
        Set<Integer> travelDays = new HashSet<>();
        for (int day : days) {
            travelDays.add(day);
        }

        int lastDay = days[days.length - 1];
        int[] dp = new int[lastDay + 1]; // dp[i] = min cost up to day i

        for (int i = 1; i <= lastDay; i++) {
            if (!travelDays.contains(i)) {
                // No travel on this day, cost same as previous day
                dp[i] = dp[i - 1];
            } else {
                // Choose minimum of three options
                int cost1 = dp[Math.max(0, i - 1)] + costs[0];
                int cost7 = dp[Math.max(0, i - 7)] + costs[1];
                int cost30 = dp[Math.max(0, i - 30)] + costs[2];

                dp[i] = Math.min(cost1, Math.min(cost7, cost30));
            }
        }

        return dp[lastDay];
    }

    public static void main(String[] args) {
        int[] days1 = {1, 4, 6, 7, 8, 20};
        int[] costs1 = {2, 7, 15};
        System.out.println("Min cost: " + mincostTickets(days1, costs1)); // Output: 11

        int[] days2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 30, 31};
        int[] costs2 = {2, 7, 15};
        System.out.println("Min cost: " + mincostTickets(days2, costs2)); // Output: 17
    }
}
