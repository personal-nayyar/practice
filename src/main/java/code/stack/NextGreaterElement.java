package code.stack;

import java.util.Stack;

/**
 Given an array arr[] of size N having elements, the task is to find the next greater element for each element of the array in order of their appearance in the array.
 Next greater element of an element in the array is the nearest element on the right which is greater than the current element.
 If there does not exist next greater of current element, then next greater element for current element is -1. For example, next greater of the last element is always -1
 Input:
 N = 4, arr[] = [1 3 2 4]
 Output:
 3 4 4 -1
 Explanation:
 In the array, the next larger element
 to 1 is 3 , 3 is 4 , 2 is 4 and for 4 ?
 since it doesn't exist, it is -1.

 N = 5, arr[] [6 8 0 1 3]
 Output:
 8 -1 1 3 -1
 Explanation:
 In the array, the next larger element to
 6 is 8, for 8 there is no larger elements
 hence it is -1, for 0 it is 1 , for 1 it
 is 3 and then for 3 there is no larger
 element on right and hence -1.

 * */
public class NextGreaterElement {
    public static void main(String[] args) {
        nextGreaterElement(new int[]{1, 3, 2, 4});
        nextGreaterElement(new int[]{6, 8, 0, 1, 3});
    }
    public static void nextGreaterElement(int[] arr){
        if(arr.length == 0)
            return;
        Stack<Integer> st =  new Stack<>();
        st.push(arr[0]);

        for (int i = 1; i < arr.length; i++) {
            while (!st.isEmpty() && arr[i] > st.peek())
                System.out.println(st.pop()+"--------"+arr[i]);
            if (st.isEmpty() || arr[i] <= st.peek())  // push element <= top to the stack as it can not be nge to curr element
                st.push(arr[i]);

        }
        while (!st.isEmpty())
            System.out.println(st.pop()+"--------"+"N");
    }
}
