package companies.coupang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OverlappingIntervals {
    public static void main(String[] args) {
        int[][] intervals = {{1,5}, {6,7}, {9,10}, {10, 12}, {11, 13}, {15, 20}};
        int[][] mergedIntervals = merge(intervals);
        for (int[] mergedInterval : mergedIntervals) {
            System.out.println(mergedInterval[0] + " " + mergedInterval[1]);
        }
    }

    public static int[][] merge(int[][] intervals) {
        List<int[]> result = new ArrayList<>();
        boolean[] merged = new boolean[intervals.length];

        for (int i = 0; i < intervals.length; i++) {
            if (merged[i]) continue;

            int start = intervals[i][0];
            int end = intervals[i][1];

            for (int j = i + 1; j < intervals.length; j++) {
                if (!merged[j] && intervals[j][0] <= end && intervals[j][1] >= start) {
                    // Merge
                    start = Math.min(start, intervals[j][0]);
                    end = Math.max(end, intervals[j][1]);
                    merged[j] = true;
                }
            }
            result.add(new int[]{start, end});
        }
        return result.toArray(new int[result.size()][]);
    }

    public static int[][] mergeOptimised(int[][] intervals) {
        if (intervals.length <= 1) return intervals;

        // Step 1: Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        result.add(current);

        // Step 2: Traverse intervals
        for (int[] interval : intervals) {
            int currentEnd = current[1];
            int nextStart = interval[0];
            int nextEnd = interval[1];

            if (nextStart <= currentEnd) {
                // Overlap -> merge
                current[1] = Math.max(currentEnd, nextEnd);
            } else {
                // No overlap -> move to next interval
                current = interval;
                result.add(current);
            }
        }

        return result.toArray(new int[result.size()][]);
    }
}
