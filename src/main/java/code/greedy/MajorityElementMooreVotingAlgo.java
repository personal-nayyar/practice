package code.greedy;

/**
 Given an array nums of size n, return the majority element.

 The majority element is the element that appears more than ⌊n / 2⌋ times.
 You may assume that the majority element always exists in the array.

 Example 1:
 Input: nums = [3,2,3]
 Output: 3

 Example 2:
 Input: nums = [2,2,1,1,1,2,2]
 Output: 2
 */
public class MajorityElementMooreVotingAlgo {
    public static void main(String[] args) {
        int[] arr =  new int[]{3, 3, 4, 2, 4, 4, 2, 4, 4};
        printMajority(arr);
        arr = new int[]{3, 3, 4, 2, 4, 4, 2, 4};
        printMajority(arr);
        printMajorityAuxSpace(arr);
    }

    static int majorityCandidate(int[] arr){  // O(N)
        int n = arr.length;
        int majIndex = 0;
        int majCount = 1;
        for (int i = 1; i < n; i++) {
            if(arr[majIndex] == arr[i])
                majCount++;
            else
                majCount--;
            if(majCount ==0){
                majIndex = i;
                majCount = 1;
            }
        }
        return arr[majIndex];
    }

    static void printMajority(int[] arr){
        int majCand = majorityCandidate(arr);
        int cnt = 0;
        for (int i = 0; i < arr.length; i++) {
            if(arr[i] == majCand)
                cnt++;
        }
        if(cnt > arr.length/2)
            System.out.println(majCand);
        else
            System.out.println("No Majority Element");
    }

    static void printMajorityAuxSpace(int[] arr){
        int n = arr.length;
        int[] freq = new int[n];
        for (int i = 0; i < n; i++) {
            freq[arr[i] - 1]++;
        }
        int majElem = -1;
        for (int i = 0; i < n; i++) {
            if(freq[i] > n/2){
                majElem = arr[freq[i]];
                break;
            }
        }
        System.out.println(majElem);

    }

}
