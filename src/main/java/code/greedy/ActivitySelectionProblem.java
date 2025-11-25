package code.greedy;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.*;

/**
 * https://leetcode.com/problems/maximum-length-of-pair-chain/description/
 You are given n activities with their start and finish times.
 Select the maximum number of activities that can be performed by a single person,
 assuming that a person can only work on a single activity at a time.
 * */
public class ActivitySelectionProblem {
    public static void main(String[] args) {
        System.out.println(maxActivityPerformed(new int[]{ 1, 3, 0, 5, 8, 5 }, new int[]{ 2, 4, 6, 7, 9, 9 }));
    }
    public static class Activity{
        int start;
        int end;

        public Activity(int start, int end){
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "Activity{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    public static List<Activity> maxActivityPerformed(int[] start, int[] finis){
        List<Activity> activities = new ArrayList<>();
        for (int i = 0; i < start.length; i++) {
            activities.add(new Activity(start[i], finis[i]));
        }
        return maxActivityPerformed(activities);
    }
    public static List<Activity> maxActivityPerformed(List<Activity> activities){
        //	1.	Sort activities by their finish time.
        //	•	Why? Because the activity that finishes earliest leaves the most room for other activities.
        //	2.	Pick activities greedily:
        //	•	Always pick the next activity that starts after or when the previous one ends.


        // Step 2: Sort activities by end time
        List<Activity> result  = new ArrayList<>();
        activities.sort((a,b) -> a.end -  b.end);
        int lastFinishTime = -1;
        for (Activity activity : activities){
            if (activity.start >= lastFinishTime){
                result.add(activity);
                lastFinishTime = activity.end;
            }
        }
        return result;
    }

    public static void selectActivities(List<Activity> activities) {
        // Sort by finish time
        activities.sort(Comparator.comparingInt(a -> a.end));

        System.out.println("Selected activities:");
        int lastFinishTime = -1;
        for (Activity act : activities) {
            if (act.start >= lastFinishTime) {
                System.out.println("(" + act.start + ", " + act.end + ")");
                lastFinishTime = act.end;
            }
        }
    }
}
/**
 * https://leetcode.com/problems/merge-intervals/description/
 * Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.
 * Example 1:
 * Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
 * Output: [[1,6],[8,10],[15,18]]
 * Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].
 *
 * Example 2:
 * Input: intervals = [[1,4],[4,5]]
 * Output: [[1,5]]
 * Explanation: Intervals [1,4] and [4,5] are considered overlapping.
 *
 * Example 3:
 * Input: intervals = [[4,7],[1,4]]
 * Output: [[1,7]]
 * Explanation: Intervals [1,4] and [4,7] are considered overlapping.
 * */
class MergeOverlappingInterval {
    public static void main(String[] args) {
        int[][] intervals = {{1,2},{2,3},{3,4},{1,3}};
        List<Interval> intervals1 = new ArrayList<>();
        for (int[] interval: intervals){
            intervals1.add(new Interval(interval[0], interval[1]));
        }
        System.out.println(mergeOverlappingInterval(intervals1));
    }

    @AllArgsConstructor
    static class Interval{
        int start;
        int end;

        @Override
        public String toString() {
            return "Interval{" +
                    "start=" + start +
                    ", end=" + end +
                    '}';
        }
    }

    public static List<Interval> mergeOverlappingInterval(List<Interval> intervals){
        List<Interval> result = new ArrayList<>();
        // check overlap
        intervals.sort(Comparator.comparingInt(a -> a.start));
        Interval last = intervals.get(0);
        result.add(last);
        for (Interval interval : intervals){
            if (last.end > interval.start){
                // overlap -> merge
                last.end =  Math.max(last.end, interval.end);
            }else{
                // no overlap -> add
                result.add(interval);
                last = interval;
            }
        }
        return result;
    }
}

/**
 maximum non-overlapping intervals
 Intervals: [1,2], [2,3], [3,4], [1,3]
 Sorted by end: [1,2], [2,3], [1,3], [3,4]

 Pick [1,2] → next non-overlapping starts at ≥2 → [2,3] → [3,4]
 Count = 3 kept
 * */

class MaximumNonOverlappingInterval{
    public static void main(String[] args) {
        int[][] intervals1 = {{1,2}, {2,3}, {3,4}, {1,3}};
        int[][] intervals2 = {{1,2}, {1,2}, {1,2}};
        int[][] intervals3 = {{1,2},{2,3}};
        System.out.println("max nonOverlapping (Example 1): " + maxNonOverlapping(intervals1));
        System.out.println("max nonOverlapping (Example 2): " + maxNonOverlapping(intervals2));
        System.out.println("max nonOverlapping (Example 3): " +maxNonOverlapping(intervals3));
    }

    public  static int maxNonOverlapping(int[][] intervals){
        List<int[]> result = new ArrayList<>();
        int lastEnd = -1;
        for (int[] interval: intervals){
            if (lastEnd <= interval[0]) { // lastEnd > currStart -> overlap
                result.add(interval);
            }
        }
        return result.size();
    }
}

/**
 * https://leetcode.com/problems/non-overlapping-intervals/description/
 * Given an array of intervals where intervals[i] = [starti, endi],
 * return the minimum number of intervals you need to remove to make the rest of the intervals non-overlapping.
 * Note that intervals which only touch at a point are non-overlapping. For example, [1, 2] and [2, 3] are non-overlapping.
 *
 * Example 1:
 * Input: intervals = [[1,2],[2,3],[3,4],[1,3]]
 * Output: 1
 * Explanation: [1,3] can be removed and the rest of the intervals are non-overlapping.
 *
 * Example 2:
 * Input: intervals = [[1,2],[1,2],[1,2]]
 * Output: 2
 * Explanation: You need to remove two [1,2] to make the rest of the intervals non-overlapping.
 *
 * Example 3:
 * Input: intervals = [[1,2],[2,3]]
 * Output: 0
 * Explanation: You don't need to remove any of the intervals since they're already non-overlapping.
 * */
class EraseOverlapIntervals{

    public static void main(String[] args) {
        int[][] intervals1 = {{1,2}, {2,3}, {3,4}, {1,3}};
        int[][] intervals2 = {{1,2}, {1,2}, {1,2}};
        int[][] intervals3 = {{1,2},{2,3}};
        System.out.println("Minimum removals (Example 1): " + minEraseOverlapIntervals(intervals1));
        System.out.println("Minimum removals (Example 2): " + minEraseOverlapIntervals(intervals2));
        System.out.println("Minimum removals (Example 3): " +minEraseOverlapIntervals(intervals3));
    }

    static int minEraseOverlapIntervals2(int[][] intervals){
        // minimum_to_remove = total_intervals - maximum_non_overlapping_intervals
        return intervals.length - MaximumNonOverlappingInterval.maxNonOverlapping(intervals);
    }

    static int minEraseOverlapIntervals(int[][] intervals){
        // sort by end time -> leave more room for overlapping interval
        Arrays.sort(intervals, (a,b) -> Integer.compare(a[1], b[1]));
        // check overlap
        int[] last = intervals[0];
        int removal = -1; // for first interval always true
        for (int[] interval: intervals){
            if (last[1] > interval[0]){
                // overlap -> merge -> incrementCount
                removal++;
            }else{
                last = interval;
            }
        }
        return removal;
    }
}


/***
 https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 Given the array points, return the minimum number of arrows that must be shot to burst all balloons.

 Input: points = [[10,16],[2,8],[1,6],[7,12]]
 Output: 2
 Explanation: The balloons can be burst by 2 arrows:
 - Shoot an arrow at x = 6, bursting the balloons [2,8] and [1,6].
 - Shoot an arrow at x = 11, bursting the balloons [10,16] and [7,12].
 */
class minArrowBurstBallon{
    public static void main(String[] args) {
        int[][] balloons = {{10,16},{2,8},{1,6},{7,12}};
        System.out.println(minArrows(balloons));

        int[][] balloons2 = {{1,2},{3,4},{5,6},{7,8}};
        System.out.println(minArrows(balloons2));

        int[][] balloons3 = {{1,2},{2,3},{3,4},{4,5}};
        System.out.println(minArrows(balloons3));
    }

    public static int minArrows(int[][] ballons){
        // sort by end
        return MaximumNonOverlappingInterval.maxNonOverlapping(ballons);
    }
}

/**
 https://leetcode.com/problems/maximum-profit-in-job-scheduling/description/
 https://leetcode.com/problems/maximum-earnings-from-taxi/description/
 https://leetcode.com/problems/maximum-number-of-events-that-can-be-attended-ii/description/
 We have n jobs, where every job is scheduled to be done from startTime[i] to endTime[i], obtaining a profit of profit[i].
 You're given the startTime, endTime and profit arrays,
 return the maximum profit you can take such that there are no two jobs in the subset with overlapping time range.

 If you choose a job that ends at time X you will be able to start another job that starts at time X.
 * */
class WeightedJobScheduling{

    static class Job{
        int start;
        int end;
        int profit;
    }
    public static void main(String[] args) {

    }

    public int maxProfit(List<Job> jobs){
        // sort job by end time
        jobs.sort(Comparator.comparingInt(j -> j.end));

        int n = jobs.size();
        int[] dp = new int[n];
        dp[0] = jobs.get(0).profit;

        for (int i = 1; i < n; i++) {
            int inclusive = jobs.get(i).profit; // include current job profit
            int lastNonConflict = findLastNonConflict(jobs, i);
            if (lastNonConflict != -1)
                    inclusive += dp[lastNonConflict];  // include profit made till last non conflict job
            dp[i] = Math.max(inclusive, dp[i-1]); // max of include this or exclude this
        }
        return dp[n-1];
    }

    // Binary Search: find last job that doesn't conflict
    static int findLastNonConflict(List<Job> jobs, int index) {
        int low = 0, high = index - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (jobs.get(mid).end <= jobs.get(index).start) {
                if (jobs.get(mid + 1).end <= jobs.get(index).start)
                    low = mid + 1;
                else
                    return mid;
            } else
                high = mid - 1;
        }
        return -1;
    }
}

/**
 https://leetcode.com/problems/maximum-number-of-events-that-can-be-attended/description/
 You are given an array of events where events[i] = [startDayi, endDayi].
 Every event i starts at startDayi and ends at endDayi.

 You can attend an event i at any day d where startDayi <= d <= endDayi. You can only attend one event at any time d.

 Return the maximum number of events you can attend.
 Input: events = [[1,2],[2,3],[3,4]]
 Output: 3
 Explanation: You can attend all the three events.
 One way to attend them all is as shown.
 Attend the first event on day 1.
 Attend the second event on day 2.
 Attend the third event on day 3.
 Example 2:

 Input: events= [[1,2],[2,3],[3,4],[1,2]]
 Output: 4
 * */
class MaxEventAttended{
    public static void main(String[] args) {
        List<Event> events = new ArrayList<>(){{
           add(new Event(1,2));
            add(new Event(2,3));
            add(new Event(3,4));
        }};
        System.out.println(maxEvent(events));

        List<Event> events2 = new ArrayList<>(){{
           add(new Event(1,2));
            add(new Event(2,4));
            add(new Event(3,5));
            add(new Event(1,2));
        }};
        System.out.println(maxEvent(events2));
    }

    @ToString
    @AllArgsConstructor
    static class Event{
        int start;
        int end;
    }

    public static int maxEvent(List<Event> events){
        events.sort(Comparator.comparingInt(a -> a.end));

        int maxDay = events.stream().mapToInt(event -> event.end).max().getAsInt();
        boolean[] days = new boolean[maxDay+1];
        int count = 0;
        for (Event e : events){
            for (int i = e.end; i>= e.start; i--) { // try to attend the event at last possible day
                if (!days[i]){
                    days[i] = true;
                    count++;
                    break;
                }
            }
        }
        return count;
    }
}

/*
Given two arrays, deadline[] and profit[], where deadline[i] is the last time unit by which the i-th job must be completed, and profit[i] is the profit earned from completing it.
Each job takes 1 unit time, and only one job can be scheduled at a time. A job earns profit only if finished within its deadline.
Find the number of jobs completed and maximum profit.
* */
class JobSequencingMaxProfit2 {
    public static void main(String[] args) {
        List<Job> jobs = Arrays.asList(
                new Job(1, 1),
                new Job(2, 2),
                new Job(2, 2),
                new Job(7, 1),
                new Job(4, 3),
                new Job(4, 5),
                new Job(3, 1)
        );
        int maxProfit = maxProfit(jobs, 7);
        System.out.println("Maximum profit in 7 days: " + maxProfit);
    }

    @ToString
    @AllArgsConstructor
    static class Job{
        // int start; // -> 1 (any jobs can be completed at day 1)
        int deadline;
        int profit;
    }

    public static int maxProfit(List<Job> jobs){
        int maxDays = jobs.stream().mapToInt(j -> j.deadline).max().getAsInt();
        return maxProfit(jobs, maxDays);
    }

    public static int maxProfit(List<Job> jobs, int maxDay){
//        jobs.sort(Comparator.comparingInt((Job j) -> j.deadline));
        jobs.sort(Comparator.comparingInt((Job j) -> j.profit).reversed());
        int[] days = new int[maxDay+1]; // 1-index based
        Arrays.fill(days, -1);
        int totalProfit = 0;
        for (Job job: jobs){
            for (int i = Math.min(job.deadline, maxDay); i >0; i--) {
                if (days[i] == -1){ // not perform activity on that day
                    days[i] = job.profit;
                    totalProfit += job.profit;
                    break; // this job done at ith day
                }
            }
        }
        System.out.println(Arrays.toString(days));
        return totalProfit;
    }
}

