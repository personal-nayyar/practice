package code.string.mix;

/**
 Find the count of all possible strings of size n. Each character of the string is either ‘R’, ‘B’ or ‘G’.
 In the final string there needs to be at least r number of ‘R’, at least b number of ‘B’ and at least g number of ‘G’ (such that r + g + b <= n).

 Input: n = 4, r = 1, g = 1, b = 1
 Output: 36
 Explanation: No. of 'R' >= 1,
 No. of ‘G’ >= 1, No. of ‘B’ >= 1
 and (No. of ‘R’) + (No. of ‘B’)
 + (No. of ‘G’) = n then
 following cases are possible:
 1. RBGR and its 12 permutation
 2. RBGB and its 12 permutation
 3. RBGG and its 12 permutation
 Hence description is 36.

 Input: n = 4, r = 2, g = 0, b = 1
 Output: 22
 Explanation: No. of 'R' >= 2,
 No. of ‘G’ >= 0, No. of ‘B’ >= 1
 and (No. of ‘R’) + (No. of ‘B’)
 + (No. of ‘G’) <= n then
 following cases are possible:
 1. RRBR and its 4 permutation
 2. RRBG and its 12 permutation
 3. RRBB and its 6 permutation
 Hence description is 22.

 * */
public class ColorfulString {
    public static void main(String[] args) {
        System.out.println(cntColorfulString(4,1,1,1));
        System.out.println(cntColorfulString(4,2,0,1));
    }
    static int cntColorfulString(int n, int r, int g, int b){
        // calculate fact upto n
        int fact[] =  new int[n+1];
        fact[0] = 1;
        for (int i = 1; i <= n; i++) { //generate factorial upto n i.e. 1!=0, 2!=2, 3!=6, 4!=12 etc
            fact[i] = fact[i-1]*i;
        }
        int rem =  n -(r+g+b);
        int sum =0;
        // choosing(combination) [n!/((n-r)!*r!)]
        for (int rr = 0; rr <= rem; rr++) { // represent choosing R
            for (int gg = 0; gg <= rem- rr; gg++) { // represent choosing G
                int bb =  rem-(rr + gg);// represent choosing B
                // arrangement (permutation n!/r!)
                sum =  sum + fact[n] /
                                (fact[rr +r] // total Red present in arrangement
                                * fact[gg +b] // total green present in arrangement
                                * fact[bb +g]); // total blue present in arrangement
            }
        }
        return sum;
    }
}
