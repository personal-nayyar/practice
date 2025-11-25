package code.dp;

/**
 Given a positive integer N, count all possible distinct binary strings of length N such that there are no consecutive 1’s.
    Examples:
    N = 1, Output = 2
    // The 2 strings are 0 and 1
    N =  2, Output = 3
    // The 3 strings are 00, 01, 10
   N = 3, Output = 5
    // The 5 strings are 000, 001, 010, 100, 101
 */
public class BinaryStringWithoutConsecutive1 {
    public static void main(String[] args) {
        System.out.println(numberOfBinaryStringWithoutConsecutiveOneRec(3));
    }

    static  int numberOfBinaryStringWithoutConsecutiveOneRec(int n){
        if(n == 1)
            return 2;
        if(n == 2)
            return 3;
        return numberOfBinaryStringWithoutConsecutiveOneRec(n-1) + numberOfBinaryStringWithoutConsecutiveOneRec(n-2);
    }

    public static int  numberOfBinaryStringWithoutConsecutiveOne(int n){
        int[] a = new int[n]; // number of string ending with 0
        int[] b = new int[n]; // number of string ending with 1
        a[0] = b[0] =1; // for n = 1
        for (int i = 1; i < n; i++) {
            a[i] =  a[i-1] // adding 0 at the end
                    +b[i-1]; // adding 1 at the end
            b[i] = a[i-1]; // since can add 0 at end, so number of string will be same as previous a[i]; but can not add 1 at end
        }
        return a[n-1]+b[n-1];
    }
}
