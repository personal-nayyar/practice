package A_interview_experiences.adobe;

import org.apache.spark.sql.sources.In;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TripletSum {

    public static void main(String[] args){
//        int[] arr = {1,2,3,4,5,6};
//        int[] res = pairSum(arr, 10);
//        System.out.println(Arrays.toString(res));
//
//        arr = new int[]{1,2,3,4,5,6,7};
//        res = pairSum(arr, 10);
//        System.out.println(Arrays.toString(res));
//
//        arr = new int[]{1,2,3,4,5,6,7,8};
//        res = pairSum(arr, 10);
//        System.out.println(Arrays.toString(res));


//        int[] arr1 = {1,2,3,4,5,6,7,8};
//        int[] res1 = pairIndexSum(arr1, 10);
//        System.out.println(Arrays.toString(res1)); // expect [0, 1]
//
//        int[] arr2 = {1,2,3,4,5,6,7,8};
//        int[] res2 = pairIndexSum(arr2, 15);
//        System.out.println(Arrays.toString(res2)); // expect [0, 7]
//
//        int[] arr3 = {1,2,3,4,5};
//        int[] res3 = pairIndexSum(arr3, 6);
//        System.out.println(Arrays.toString(res3)); // expect [0, 1]
//
//        int[] arr4 = {1,2,3,4,5};
//        int[] res4 = pairIndexSum(arr4, 7);
//        System.out.println(Arrays.toString(res4)); // expect null

        int[] arr1 = {1,2,3,4,5,6,7};
        int[] res1 = tripletIndexSum(arr1, 10);
        System.out.println(Arrays.toString(res1)); // expect [0, 1, 2]

        int[] arr2 = {1,2,3,4,5};
        int[] res2 = tripletIndexSum(arr2, 10);
        System.out.println(Arrays.toString(res2)); // expect [-1, -1, -1]

        int[] arr3 = {1,2,3,4,5};
        int[] res3 = tripletIndexSum(arr3, 15);
        System.out.println(Arrays.toString(res3)); // expect [-1, -1, -1]

        int[] arr4 = {1,2,3,4,5};
        int[] res4 = tripletIndexSum(arr4, 1);
        System.out.println(Arrays.toString(res4)); // expect [-1, -1, -1]

        int[] arr5 = {1,2,3,4,5};
        int[] res5 = tripletIndexSum(arr5, 100);
        System.out.println(Arrays.toString(res5)); // expect [-1, -1, -1]

        int[] arr6 = {1,2,3,4,5};
        int[] res6 = tripletIndexSum(arr6, 12);
        System.out.println(Arrays.toString(res6)); // expect [0, 1, 2]
    }

    public static int[] tripletIndexSum(int[] arr, int target){
//        Map<Integer, Integer> map = new HashMap<>();
//        for (int i = 0; i < arr.length; i++) {
//            map.put(arr[i], i);
//        }
//        for (int i = 0; i < arr.length; i++) {
//            for (int j = i+1; j < arr.length; j++) {
//                int sum = arr[i] + arr[j];
//                int comp =  target - sum;
//                if (map.containsKey(comp) && i != map.get(comp) && j != map.get(comp))
//                    return new int[]{i,j,map.get(comp)};
//            }
//        }

        // fix ith element
        for (int i = 0; i < arr.length; i++) {
            int target2 =  target - arr[i]; // i is fixed, now find pair
            Map<Integer, Integer> map = new HashMap<>();
            for (int j = i+1; j < arr.length; j++) {
                int comp =  target2 -  arr[j];
                if (map.containsKey(comp))
                    return new int[]{i,map.get(comp),j};
                map.put(arr[j], j);
            }
        }
        return new int[]{-1,-1,-1};
    }

    public static int[] pairIndexSum(int[] arr, int target){
        Map<Integer, Integer> indexMap = new HashMap();
        for (int i = 0; i < arr.length; i++) {
            int complement = target - arr[i];

            if (indexMap.containsKey(complement))
                return new int[]{i, indexMap.get(complement)};
            indexMap.put(arr[i], i);
        }
        return new int[]{-1,-1};
    }

    public static int[] pairSum(int[] arr, int target){
        Arrays.sort(arr);
        int i=0, j=arr.length-1;
        while(i<j){
            int sum =  arr[i] + arr[j];
            if(sum == target)
                return new int[]{arr[i],arr[j]};
            else if (sum < target)
                i++;
            else
                j++;
        }
        return new int[]{-1,-1};
    }
}
