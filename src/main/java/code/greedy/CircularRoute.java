package code.greedy;

/**
 * There are N petrol pumps along a circular route, where the amount of petrol at pump i is petrol[i].
 * <p>  You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from pump i to its next pump (i+1).
 * You begin the journey with an empty tank at one of the petrol pumps.
 * <p>  Return the starting petrol pump's index if you can travel around the circuit once in the clockwise direction, otherwise return -1.
 * <p>  Note: If there exists a solution, it is guaranteed to be unique.
 * <p>  Example 1:
 *  Input: petrol = [1,2,3,4,5],
 *           cost = [3,4,5,1,2]
 *  Output: 3
 *  Explanation:
 *  If you start at station 3 (index 3) and fill up with 4 unit of gas.
 *  Your tank = 0 + 4 = 4
 *  Travel to station 4. Your tank = 4 - 1 + 5 = 8
 */
public class CircularRoute {
    public static void main(String[] args) {
        // Test case 1: Example from problem statement
        int[] petrol1 = {1, 2, 3, 4, 5};
        int[] cost1 = {3, 4, 5, 1, 2};
        System.out.println("Test Case 1 (Expected: 3): " + circularRoute(petrol1, cost1));
        
        // Test case 2: Single pump with enough petrol
        int[] petrol2 = {5};
        int[] cost2 = {3};
        System.out.println("Test Case 2 (Expected: 0): " + circularRoute(petrol2, cost2));
        
        // Test case 3: Single pump with not enough petrol
        int[] petrol3 = {2};
        int[] cost3 = {3};
        System.out.println("Test Case 3 (Expected: -1): " + circularRoute(petrol3, cost3));
        
        // Test case 4: No solution (total petrol < total cost)
        int[] petrol4 = {2, 3, 4};
        int[] cost4 = {3, 4, 5};
        System.out.println("Test Case 4 (Expected: -1): " + circularRoute(petrol4, cost4));
        
        // Test case 5: Starting point is the last element
        int[] petrol5 = {4, 5, 1, 2, 3};
        int[] cost5 =   {1, 2, 3, 4, 5};
        System.out.println("Test Case 5 (Expected: 0): " + circularRoute(petrol5, cost5));
        
        // Test case 6: All pumps have same petrol and cost (circular route possible from any point)
        int[] petrol6 = {2, 2, 2, 2};
        int[] cost6 = {2, 2, 2, 2};
        System.out.println("Test Case 6 (Expected: 0): " + circularRoute(petrol6, cost6));
    }

    static int circularRoute(int[] petrol, int[] cost) {
        int n = petrol.length;
        int start = 0;
        int currPetrol = 0;
//        System.out.println(Arrays.toString(petrol));
//        System.out.println(Arrays.toString(cost));
        while(start < n){
            currPetrol =  petrol[start] - cost[start];
//            System.out.println("currPetrol1: "+currPetrol);
            int i = (start+1)%n;
            while(currPetrol >= 0 && i != start){
                currPetrol += (petrol[i] - cost[i]);
//                System.out.println("currPetrol2 : "+currPetrol);
                i =  (i+1)%n;
            }
            if (i == start && currPetrol >= 0)
                return i;
            start++;
        }
        return -1;
    }
}