package A_interview_experiences.adobe;


import java.util.PriorityQueue;

public class KSortedArray {
    public static int[] sortKSorted(int[] arr, int k){
        int n =  arr.length;
        PriorityQueue<Integer> mihHeap = new PriorityQueue<>(); // default -> minHeap

        int i = 0;
        // insert first k+1 element
        for (;i < k+1; i++) {
            mihHeap.offer(arr[i]);
        }

        int index = 0;
        int[] res = new int[n];
        while (i < n){
            // deq min and add one ele
            res[index++] = mihHeap.poll();
            mihHeap.offer(arr[i]);
        }

        while (!mihHeap.isEmpty()){
            res[index++] = mihHeap.poll();
        }
        return res;
    }
}
