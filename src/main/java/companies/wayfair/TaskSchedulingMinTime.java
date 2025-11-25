package companies.wayfair;

import java.util.*;

/*
Given an array task_memory, representing the memory requirements for each task, and another array task_type, representing the type of each task, along with an integer max_memory which is the maximum memory the server can use to process two tasks concurrently, your function should calculate the minimum amount of time required to process all tasks. Each task requires 1 unit of time to process. The server can process two tasks at the same time only if:

They are of the same type.
Their combined memory usage does not exceed max_memory.
Example 1:

Input: n = 4, task_memory = [7, 2, 3, 9], task_type = [1, 2, 1, 3], max_memory = 10
Output: 3

Explanation:
- Tasks 0 (type 1, memory 7) and 2 (type 1, memory 3) can be processed together in 1 unit of time because their combined memory (10) matches the max_memory limit.
- Task 1 (type 2, memory 2) and Task 3 (type 3, memory 9) must be processed separately, taking 1 unit of time each due to differing types and exceeding memory constraints.
Thus, the total time required is 1 + 1 + 1 = 3 units.
https://enginebogie.com/public/question/optimal-task-scheduling-with-memory-and-type-constraints/869?srsltid=AfmBOoqzR9RJDcImVNYqj5NHYCe93RLCoNu7aYwX0RCWlMptcYiXJ5Rb&utm_source=chatgpt.com
**/
// Wayfair R0
public class TaskSchedulingMinTime {

    public static int getMinTime(int[] taskMemory, int[] taskType, int maxMemory) {
        // Group memory requirements by task type
        Map<Integer, List<Integer>> typeToMemory = new HashMap<>();

        for (int i = 0; i < taskMemory.length; i++) {
            typeToMemory.computeIfAbsent(taskType[i], k -> new ArrayList<>())
                    .add(taskMemory[i]);
        }

        int totalTime = 0;

        // Process each type independently
        for (List<Integer> memories : typeToMemory.values()) {
            Collections.sort(memories);
            int i = 0, j = memories.size() - 1;
            int time = 0;

            while (i <= j) {
                if (i == j) { // only one left
                    time++;
                    break;
                }

                if (memories.get(i) + memories.get(j) <= maxMemory) {
                    // Can process both together
                    i++;
                    j--;
                } else {
                    // Process the larger one alone
                    j--;
                }
                time++;
            }

            totalTime += time;
        }

        return totalTime;
    }

    // Example test
    public static void main(String[] args) {
        int[] taskMemory = {7, 2, 3, 9};
        int[] taskType = {1, 2, 1, 3};
        int maxMemory = 10;

        System.out.println("Minimum time: " + getMinTime(taskMemory, taskType, maxMemory));
        // Output: 3
    }
}
