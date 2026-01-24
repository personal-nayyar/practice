package leetcode.array;

import java.util.Arrays;

public class ProductExceptSelf {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(productExpectSelf(new int[]{1,2,3,4})));
    }

    public static int[] productExpectSelf2(int[] arr){
        int prod = 1;
        for (int el: arr)
            prod *= el;
        for (int i = 0; i < arr.length; i++) {
            arr[i] = prod/arr[i];
        }
        return arr;
    }

    public static int[] productExpectSelf(int[] arr){
        int n =  arr.length;
        int[] prefixProd = new int[n], suffixProd =  new int[n];
        prefixProd[0] = 1; suffixProd[n-1] = 1;
//        System.out.println(Arrays.toString(arr));
        for (int i = 1; i < n; i++) {
            prefixProd[i] =  prefixProd[i-1] * arr[i-1];
        }
//        System.out.println(Arrays.toString(prefixProd));
        for (int i = n-2; i >=0; i--) {
            suffixProd[i] =  suffixProd[i+1] * arr[i+1];
        }
//        System.out.println(Arrays.toString(suffixProd));

        int[] ans =  new int[n];
        for (int i = 0; i < n; i++) {
            ans[i] = prefixProd[i] * suffixProd[i];
        }
        return ans;
    }
}
