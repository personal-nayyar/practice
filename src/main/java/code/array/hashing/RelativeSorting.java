package code.array.hashing;

import java.util.*;

/**
 Given two arrays A1[] and A2[], sort A1 in such a way that the relative order among the elements will be same as those are in A2.
 For the elements not present in A2, append them at last in sorted order.
 Input: A1[] = {2, 1, 2, 5, 7, 1, 9, 3, 6, 8, 8}
 A2[] = {2, 1, 8, 3}
 Output: A1[] = {2, 2, 1, 1, 8, 8, 3, 5, 6, 7, 9}

 Input: A1[] = {4, 5, 1, 1, 3, 2}
 A2[] = {3, 1}
 Output: A1[] = {3, 1, 1, 2, 4, 5}

 * */
public class RelativeSorting {
    public static int[] relativeSort(int[] A1, int[] A2) {
        Map<Integer, Integer> freq = new HashMap<>();

        // Step 1: Store frequencies of A1
        for (int num : A1) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        List<Integer> result = new ArrayList<>();

        // Step 2: Place elements of A2 in result
        for (int val : A2) {
            if (freq.containsKey(val)) {
                int count = freq.get(val);
                for (int i = 0; i < count; i++) {
                    result.add(val);
                }
                freq.remove(val); // remove so we only sort leftovers later
            }
        }

        // Step 3: Add remaining elements (not in A2)
        List<Integer> remaining = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
            int val = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                remaining.add(val);
            }
        }

        // Step 4: Sort remaining
        Collections.sort(remaining);

        // Step 5: Append remaining
        result.addAll(remaining);

        // Convert back to array
        return result.stream().mapToInt(i -> i).toArray();
    }

}
