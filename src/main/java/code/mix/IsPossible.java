package code.mix;
/*
* allowed operation
* 1. (x,y) -> (x+y,y)
* 2. (x,y) -> (x,y+x)'
* 1≤ a,b,c,d ≤ 1000
* (a,b) -> (c,d)
* */
public class IsPossible {
    public static void main(String[] args) {
        System.out.println(isPossible(1,4,5,9));
    }

    public static  boolean isPossible(int a, int b, int c, int d){
        return isPossibleUtil(a,b,c,d);
    }

    public static boolean isPossibleUtil(int a, int b, int c, int d){
        System.out.println(a+","+b);
        // base condition
        if(a==c && b==d)
            return true;
        if (a > c || b > d)
            return false;
        return isPossibleUtil(a,a+b, c,d) // check if (x+y,y) can lead to (c,d)
                || isPossibleUtil(a+b,b, c,d); // check if (x,x+y) can lead to (c,d)
    }

}
