package code.greedy;

import lombok.ToString;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/*
Given two arrays, deadline[] and profit[], where deadline[i] is the last time unit by which the i-th job must be completed, and profit[i] is the profit earned from completing it.
Each job takes 1 unit time, and only one job can be scheduled at a time. A job earns profit only if finished within its deadline.
Find the number of jobs completed and maximum profit.
* */
public class JobSequencingMaxProfit {

}


@ToString
class Job{
    int deadline;
    int payment;
    Job(int deadline, int payment){
        this.deadline = deadline;
        this.payment = payment;
    }
}


class Main {
    public static void main(String[] args) {
        System.out.println(Instant.now());
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

    public static int maxProfit(List<Job> jobs, int days) {
        // Sort jobs by payment descending
        jobs.sort((a, b) -> b.payment - a.payment);
//        System.out.println("jobs: "+jobs);

        int[] slots = new int[days + 1]; // 1-indexed days
        Arrays.fill(slots, -1);

        int total = 0;
        for (Job job : jobs) {
            for (int d = Math.min(days, job.deadline); d > 0; d--) {
                if (slots[d] == -1) {
                    slots[d] = job.payment;
                    total += job.payment;
                    break;
                }
            }
        }
        return total;
    }

    public static int maxProfit2(List<Job> jobs, int maxDays) {
        // 1. Sort jobs by payment descending
        jobs.sort((a, b) -> b.payment - a.payment);

        // 2. Array to track occupied days (1-indexed)
        boolean[] slot = new boolean[maxDays + 1];
        int total = 0;

        for (Job job : jobs) {
            // Try to schedule job at latest available slot ≤ deadline
            for (int d = Math.min(maxDays, job.deadline); d > 0; d--) {
                if (!slot[d]) {      // if slot is free
                    slot[d] = true;  // mark as occupied
                    total += job.payment;
                    break;
                }
            }
        }
        return total;
    }

    public static int maxProfit3(List<Job> jobs, int maxDays) {
        // 1. Sort jobs by payment descending
        jobs.sort((a, b) -> b.payment - a.payment);

        // 2. Track occupied days (0 = free, 1 = occupied)
        int[] slots = new int[maxDays]; // 0-indexed: day 0 .. maxDays-1

        int total = 0;

        for (Job job : jobs) {
            // Try to schedule job on the latest possible day before its deadline
            // job.deadline is 1-indexed, so map to 0-indexed array
            for (int d = Math.min(job.deadline, maxDays) - 1; d >= 0; d--) {
                if (slots[d] == 0) { // slot free
                    slots[d] = 1;    // mark occupied
                    total += job.payment;
                    break;
                }
            }
        }

        return total;
    }
}