package code.backtracking_dp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Given a positive integer N and an array arr[] of size K consisting of binary string where each string is of size N,
 * the task is to find all the binary strings of size N that are not present in the array arr[].
 * Input: N = 3, arr[] = {“101”, “111”, “001”, “010”, “011”, “100”, “110”}
 * Output: 000
 * Explanation: Only 000 is absent in the given array.
 *
 * Input: N = 2, arr[] = {“00”, “01”}
 * Output: 10 11
 * */
public class BinaryNotPresentInList {

    public static void main(String[] args) {
        List<String> givenList = Arrays.asList("101", "110", "001", "100");
        BinaryGeneratorLengthN.generateAllBinaryOfLengthN(3);
        BinaryGeneratorLengthN.allBinary.removeAll(givenList);
        System.out.println(BinaryGeneratorLengthN.allBinary);
    }
}

class BinaryGeneratorLengthN{
    public static final List<String> allBinary = new ArrayList<>();
    public static void main(String[] args) {
        int n =  3;
        generateAllBinaryOfLengthN(n);
        System.out.println(allBinary);
    }


    public static void generateAllBinaryOfLengthN(int n){
        int[] arr =  new int[n];
        generateAllBinaryOfLengthNUtil(arr, n-1);
    }

    private static void generateAllBinaryOfLengthNUtil(int[] arr, int i){
        if(i<0)
        // covert int[] to string and add to list
        {allBinary.add(Arrays.stream(arr).mapToObj(String::valueOf).collect(Collectors.joining()));return;}
        // put arr[i] = 0 and generate all possible permutations for i-1 index
        arr[i] = 0;
        generateAllBinaryOfLengthNUtil(arr, i-1);

        // put arr[i] = 1 and generate all possible permutations for i-1 index
        arr[i] = 1;
        generateAllBinaryOfLengthNUtil(arr, i-1);
    }
}