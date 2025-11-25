package must_do.arrays;

import utils.DSAUtils;

import java.util.Arrays;
import java.util.PriorityQueue;

class SecondLargest {
    public static void main(String[] args) {
        System.out.println(secondLargest(new int[]{12, 35, 1, 10, 34, 1}));
        System.out.println(secondLargest(new int[]{10, 5, 10}));
        System.out.println(secondLargest(new int[]{10, 10, 10}));
    }

    public static int secondLargest(int[] arr){
        int largest = Integer.MIN_VALUE, secondLargest = Integer.MIN_VALUE;
        for (int el: arr){
            largest = Math.max(largest, el);
            if(el < largest){
                secondLargest = Math.max(secondLargest, el);
            }
        }
        return secondLargest;
    }
}

class ThirdLargest{
    public static void main(String[] args) {
//        System.out.println(thirdLargest(new int[]{1, 14, 2, 16, 10, 20}));
//        System.out.println(thirdLargest(new int[]{19, -10, 20, 14, 2, 16, 10}));

        System.out.println(thirdLargest2(new int[]{1, 14, 2, 16, 10, 20}));
        System.out.println(thirdLargest2(new int[]{19, -10, 20, 14, 2, 16, 10}));
    }

    // use bubble sort upto 3x time
    public static int thirdLargest(int[] arr){ // O(kn)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < arr.length-i-1; j++) {
                if (arr[j] > arr[j+1])
                    DSAUtils.swap(arr, j, j+1);
            }
            System.out.println(Arrays.toString(arr));
        }
        return arr[arr.length-3];
    }

    public static int thirdLargest2(int[] arr){
        int largest = Integer.MIN_VALUE, secLargest = Integer.MIN_VALUE, thirdLargest = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > largest){
                thirdLargest = secLargest;
                secLargest = largest;
                largest = arr[i];
            } else if (arr[i] > secLargest){
                thirdLargest =  secLargest;
                secLargest = arr[i];
            } else if (arr[i] > thirdLargest){
                thirdLargest = arr[i];
            }
        }
        return thirdLargest;
    }
    /*
    {1, 14, 2, 16, 10, 20}
                        j
    largest = 20
    second = 16
    thirdLargest = 14
    * */


}

public class KthLargest{
    // quickSelect
    public static void main(String[] args) {
        System.out.println(kthLargest(new int[]{3,2,1,5,6,4}, 2));
        System.out.println(kthLargest(new int[]{3,2,3,1,2,4,5,5,6}, 4));
    }

    public static int kthLargest(int[] arr, int k){
        return kthLargestUtil(arr, 0, arr.length-1, arr.length- k, arr.length);
    }

    public static int kthLargestUtil(int[] arr, int low, int high, int index, int n){
        if (low <= high){
            int pi = partition(arr, low, high);
//            System.out.println("pi:"+pi);
            System.out.println(Arrays.toString(arr));
            if (pi == index)
                return arr[pi];
            else if (pi < index) {
                // search in right half
                return kthLargestUtil(arr, pi+1, high, index, n);
            } else {
                return kthLargestUtil(arr, low, pi-1, index, n);
            }
        }
        return -1;
    }


    public static int partition(int[] arr, int low, int high){
//        System.out.println("low:"+low+", high:"+high);
        int pi = arr[high], i = low-1;
        // place el to its correct position and return index
        for (int j = low; j < high; j++) {
//            System.out.println("i:"+i+", j:"+j);
            if (arr[j] <= pi){
                DSAUtils.swap(arr, ++i, j);
            }
        }
        DSAUtils.swap(arr, ++i, high);
        return i;
    }

    public static int kthLargestMinHeap(int[] arr, int k){
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int el: arr){
            minHeap.offer(el);
            if (minHeap.size() > k)
                minHeap.poll();
        }
        return minHeap.peek();
    }
}
