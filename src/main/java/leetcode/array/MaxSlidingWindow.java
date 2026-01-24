package leetcode.array;

import org.apache.ivy.util.HostUtil;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

public class MaxSlidingWindow {
    public static void main(String[] args) {
//        System.out.println(Arrays.toString(maxSlidingWindow(new int[]{1,3,-1,-3,5,3,6,7}, 3)));
//        System.out.println(Arrays.toString(maxSlidingWindow(new int[]{1}, 1)));

        System.out.println(Arrays.toString(maxSlidingWindow2(new int[]{1,3,-1,-3,5,3,6,7}, 3)));
        System.out.println(Arrays.toString(maxSlidingWindow2(new int[]{1}, 1)));
    }

    public static int[] maxSlidingWindow(int[] nums, int k){
        // [val, index]
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a,b) ->  b[0] -a[0]);

        int n =  nums.length;
        int[] maxArray = new int[n-k+1];
        for (int i = 0; i < k; i++) {
            maxHeap.offer(new int[]{nums[i], i});
        }
        maxArray[0] = maxHeap.peek()[0];
        for (int i = k; i < n; i++) {
            maxHeap.offer(new int[]{nums[i], i});

            // remove all index outside the window
            while (!maxHeap.isEmpty() && maxHeap.peek()[1] < i-k+1){
                maxHeap.poll();
            }

            maxArray[i-k+1] =  maxHeap.peek()[0];
        }
        return maxArray;
    }

    public static int[] maxSlidingWindow2(int[] nums, int k){
        int n = nums.length;
        // [val, index]
        Deque<int[]> deque = new ArrayDeque<>();
        int[] maxWindow = new int[n-k+1];
        for (int i = 0; i < n; i++) {
            // remove element which are not part of the current window
            while (!deque.isEmpty() && deque.peekFirst()[1] < i-k+1)
                deque.pollFirst();

            // 2️⃣ Remove smaller elements from the back
            // because they can't be max if current element is bigger
            while(!deque.isEmpty() && deque.peekLast()[0] < nums[i])
                deque.pollLast();

            // add element to the deque
            deque.offerLast(new int[]{nums[i], i});

            // if window is formed, record the max
            if (i-k+1 >= 0){
                maxWindow[i-k+1] = deque.peekFirst()[0];
            }
        }
        return maxWindow;
    }
}
