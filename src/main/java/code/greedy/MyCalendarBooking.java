package code.greedy;

import java.util.Map;
import java.util.TreeMap;

/**
 https://leetcode.com/problems/my-calendar-i/description/
 You are implementing a program to use as your calendar. We can add a new event if adding the event will not cause a double booking.

 A double booking happens when two events have some non-empty intersection (i.e., some moment is common to both events.).

 The event can be represented as a pair of integers startTime and endTime that represents a booking on the half-open interval [startTime, endTime), the range of real numbers x such that startTime <= x < endTime.

 Implement the MyCalendar class:

 MyCalendar() Initializes the calendar object.
 boolean book(int startTime, int endTime) Returns true if the event can be added to the calendar successfully without causing a double booking. Otherwise, return false and do not add the event to the calendar.


 Example 1:

 Input
 ["MyCalendar", "book", "book", "book"]
 [[], [10, 20], [15, 25], [20, 30]]
 Output
 [null, true, false, true]

 Explanation
 MyCalendar myCalendar = new MyCalendar();
 myCalendar.book(10, 20); // return True
 myCalendar.book(15, 25); // return False, It can not be booked because time 15 is already booked by another event.
 myCalendar.book(20, 30); // return True, The event can be booked, as the first event takes every time less than 20, but not including 20.
 * */
interface IMyCalendar{
    boolean book(int start, int end);
}
public class MyCalendarBooking implements IMyCalendar{
    // Key -> start, value -> end
    TreeMap<Integer, Integer> bookings = new TreeMap<>();

    public boolean book(int start, int end){
        Map.Entry<Integer, Integer> prev = bookings.floorEntry(start); // O(logn)
        if (prev!= null && prev.getValue() > start){
            // overlap with prev
            return false;
        }

        Map.Entry<Integer, Integer> next = bookings.ceilingEntry(start); // O(logn)
        if (next != null && next.getKey() < end){
            // overlap with prev
            return false;
        }
        bookings.put(start, end);
        return true;
    }

//    List<int[]> bookings = new ArrayList<>();
//
//    public boolean book2(int start, int end){
//        for (int[] b : bookings){
//            int b_start = b[0];
//            int b_end = b [1];
//            if (start < b_end && end > b_start){
//                // overlap
//                return false;
//            }
//        }
//        bookings.add(new int[]{start, end});
//        return true;
//    }
}

/**
 * https://leetcode.com/problems/my-calendar-iii/description/
 A k-booking happens when k events have some non-empty intersection (i.e., there is some time that is common to all k events.)

 You are given some events [startTime, endTime), after each given event,
 return an integer k representing the maximum k-booking between all the previous events.

 Implement the MyCalendarThree class:

 MyCalendarThree() Initializes the object.
 int book(int startTime, int endTime) Returns an integer k representing the largest integer such that there exists a k-booking in the calendar.


 Example 1:

 Input
 ["MyCalendarThree", "book", "book", "book", "book", "book", "book"]
 [[], [10, 20], [50, 60], [10, 40], [5, 15], [5, 10], [25, 55]]
 Output
 [null, 1, 1, 2, 3, 3, 3]

 Explanation
 MyCalendarThree myCalendarThree = new MyCalendarThree();
 myCalendarThree.book(10, 20); // return 1
 myCalendarThree.book(50, 60); // return 1
 myCalendarThree.book(10, 40); // return 2
 myCalendarThree.book(5, 15); // return 3
 myCalendarThree.book(5, 10); // return 3
 myCalendarThree.book(25, 55); // return 3
 * */
class MaxOverlapCountAtTimeSeries{
    // Start -> +1
    // end -> -1
    static TreeMap<Integer, Integer> timelineMap =  new TreeMap<>();
    public static int book(int start, int end){
        timelineMap.put(start, timelineMap.getOrDefault(start, 0) +1); // O(logn)
        timelineMap.put(end, timelineMap.getOrDefault(end, 0) -1); // O(logn)

        int active = 0;
        int maxOverlap = 0;
        for (int delta: timelineMap.values()){ // O(n)
            active += delta;
            maxOverlap = Math.max(maxOverlap, active);
        }
        return maxOverlap;
    }
}

/**
 https://leetcode.com/problems/my-calendar-ii/description/
 You are implementing a program to use as your calendar. We can add a new event if adding the event will not cause a triple booking.

 A triple booking happens when three events have some non-empty intersection (i.e., some moment is common to all the three events.).

 The event can be represented as a pair of integers startTime and endTime that represents a booking on the half-open interval [startTime, endTime), the range of real numbers x such that startTime <= x < endTime.

 Implement the MyCalendarTwo class:

 MyCalendarTwo() Initializes the calendar object.
 boolean book(int startTime, int endTime) Returns true if the event can be added to the calendar successfully without causing a triple booking. Otherwise, return false and do not add the event to the calendar.

 Example 1:
 Input
 ["MyCalendarTwo", "book", "book", "book", "book", "book", "book"]
 [[], [10, 20], [50, 60], [10, 40], [5, 15], [5, 10], [25, 55]]
 Output
 [null, true, true, true, false, true, true]
 Explanation
 MyCalendarTwo myCalendarTwo = new MyCalendarTwo();
 myCalendarTwo.book(10, 20); // return True, The event can be booked.
 myCalendarTwo.book(50, 60); // return True, The event can be booked.
 myCalendarTwo.book(10, 40); // return True, The event can be double booked.
 myCalendarTwo.book(5, 15);  // return False, The event cannot be booked, because it would result in a triple booking.
 myCalendarTwo.book(5, 10); // return True, The event can be booked, as it does not use time 10 which is already double booked.
 myCalendarTwo.book(25, 55); // return True, The event can be booked, as the time in [25, 40) will be double booked with the third event, the time [40, 50) will be single booked, and the time [50, 55) will be double booked with the second event.
 */
class MaxKOverlapBooking{
    static TreeMap<Integer, Integer> timelineMap = new TreeMap<>();

    public static void main(String[] args) {
        /*
        ["MyCalendarTwo", "book", "book", "book", "book", "book", "book"]
        [[], [10, 20], [50, 60], [10, 40], [5, 15], [5, 10], [25, 55]]
        Output
        [null, true, true, true, false, true, true]
        * */
        System.out.println(book(10,20, 3));
        System.out.println(book(50,60, 3));
        System.out.println(book(10,40, 3));
        System.out.println(book(5,15, 3));
        System.out.println(book(5,10, 3));
        System.out.println(book(25,55, 3));

    }

    static boolean book(int start, int end, int maxAllowed) { // O(n)
        timelineMap.put(start, timelineMap.getOrDefault(start, 0) + 1); // O(logn)
        timelineMap.put(end, timelineMap.getOrDefault(end, 0) - 1); // O(logn)
        int activeCount = 0;
        for (int val : timelineMap.values()) { // O(n)
            activeCount += val;
            if (activeCount >= maxAllowed){
                // revert this booking | undo this booking from map
                timelineMap.put(start, timelineMap.get(start) -1);
                timelineMap.put(end, timelineMap.get(end) +1);
                return false;
            }
        }
        return true;
    }
}



/**
 https://www.hackerearth.com/problem/algorithm/minimum-platforms-2/
 First line of the input file contains t , the total number of test cases.

 For each test case:

 First line contains n, the total number of trains.

 Second line contains n arrival times for n trains, where ith arrival time arrival[i] is for the ith train

 Third line contains n departure times for n trains, where ith departure time departure[i] is for the ith train

 Note: Time intervals are in the 24-hour format (HHMM) , where the first two characters represent hour (between 00 to 23 ) and the last two characters represent minutes (between 00 to 59).
 * */
class MinTrainPlatform{
    static Map<Integer, Integer> timelineMap = new TreeMap<>();
    public static void main(String[] args) {
        int[] arr = {900, 940, 950, 1100, 1500, 1800};
        int[] dep = {910, 1200, 1120, 1130, 1900, 2000};
        System.out.println(minPlatform(arr, dep));
    }

    public static int minPlatform(int[] arr, int[] dep){
        int n =  arr.length;
        int[][] trains = new int[n][2];
        for (int i = 0; i < n; i++) {
            trains[i] = new int[]{arr[i], dep[i]};
        }
        return minPlatform(trains);
    }
    public static int minPlatform(int[][] trains){
        for (int[] train: trains){ // O(n)
            int arr = train[0];
            int dep = train[1];
            timelineMap.put(arr, timelineMap.getOrDefault(arr, 0)+1); // O(logn)
            timelineMap.put(dep, timelineMap.getOrDefault(dep, 0)-1); // O(logn)
        }

        int reqPlat = 0;
        int activeCount = 0;
        for (int val: timelineMap.values()){
            activeCount += val;
            reqPlat = Math.max(reqPlat, activeCount);
        }
        return reqPlat;
    }
}