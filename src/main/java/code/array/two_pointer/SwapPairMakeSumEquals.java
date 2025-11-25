package code.array.two_pointer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;
import static utils.DSAUtils.Pair;

/**
 Given two arrays of integers, find a pair of values (one value from each array)
 that you can swap to give the two arrays the same sum.
 Input: A[] = {4, 1, 2, 1, 1, 2}, B[] = {3, 6, 3, 3}
 Output: {1, 3}
 Sum of elements in A[] = 11
 Sum of elements in B[] = 15
 To get same sum from both arrays, we
 can swap following values:
 1 from A[] and 3 from B[]
 Input: A[] = {5, 7, 4, 6}, B[] = {1, 2, 3, 8}
 Output: 6 2
 * */
public class SwapPairMakeSumEquals {
    public static void main(String[] args) {
        System.out.println(findPairMakeSumEqual(new int[]{4, 1, 2, 1, 1, 2}, new int[]{3, 6, 3, 3}));
        System.out.println(findPairMakeSumEqualSpaceOptimised(new int[]{4, 1, 2, 1, 1, 2}, new int[]{3, 6, 3, 3}));
    }


    static int getTarget(int a1[], int a2[]){ // O(m*n) if not used hashing
        int sum1 = Arrays.stream(a1).sum();
        int sum2 = Arrays.stream(a2).sum();
        if ((sum1-sum2)%2 != 0)
            return 0;
        return (sum1-sum2)/2;
    }
    static Pair findPairMakeSumEqual(int a1[], int a2[]){ // O(m+n)
        int target = getTarget(a1,a2);
        System.out.println(target);
        if(a1.length < a2.length){ // consider a2 as smaller array
            int[] temp =  a1;
            a1 = a2;
            a2 = temp;
        }
        HashSet<Integer> lookupSet =  new HashSet<>(Arrays.stream(a2).boxed().collect(Collectors.toSet()));
        for (int i = 0; i < a1.length; i++) {
            if (lookupSet.contains(a1[i]-target))
                return new Pair(a1[i], a1[i]-target);
        }
        return new Pair(-1,-1);
    }

    static Pair findPairMakeSumEqualSpaceOptimised(int a1[], int a2[]){  // O(mlogm+nlogn)
        int target = getTarget(a1,a2);
        Arrays.sort(a1);
        Arrays.sort(a2);

        int i=0,j=0;
        while(i<a1.length && j < a2.length){
            int diff =  a1[i]-a2[j];
            if (diff== target)
                return new Pair(a1[i], a2[j]);
            else if (diff > target) //  increment i to reduce diff
                i++;
            else
                j++;
        }
        return new Pair(-1,-1);
    }
}
