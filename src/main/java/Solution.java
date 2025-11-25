
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum CAR{
    SADAN,
    SUV,
    MINI
}

enum BIKE{
    SPORT,
    SIMPLE
}


class Dog{
    protected void bark(){
        System.out.println("A");
    }
}

class Puppy extends Dog{
    public void bark(){
        System.out.println("B");
    }
}

class Runner{
    public static void main(String[] args) {
        CAR o1 = CAR.SADAN;
        BIKE o2 = BIKE.SPORT;
    //    System.out.println(o1 == o2);
        System.out.println(o1.equals(o2));

    }
}

public class Solution {
    public static void main(String[] args) {
        System.out.println(canPlaceFlowers(new int[]{0}, 1));
        System.out.println(canPlaceFlowers(new int[]{1,0}, 1));
        System.out.println(canPlaceFlowers(new int[]{1,0,0,0,1}, 2));
        System.out.println(canPlaceFlowers(new int[]{0,1,0}, 1));
    }

    public static boolean canPlaceFlowers(int[] flowerbed, int n) {
        int prev = 0, next = 0;
        for (int i = 0; i < flowerbed.length; i++) {
            if (flowerbed[i] == 0){
                 prev = i == 0 || flowerbed[i-1] == 0 ? 0: 1;
                 next = i == flowerbed.length -1 || flowerbed[i+1] == 0 ? 0 : 1;
                 if(prev == 0 && next == 0) {
                     flowerbed[i] = 1;
                     n--;
                 }
            }
            if (n == 0)
                return true;
        }
        return n == 0 ? true : false;
    }

    static class GcdOfString{
        public static String gcdOfStrings(String str1, String str2) {
            int n1 = str1.length(), n2 = str2.length();
            if((str1+str2).equals(str2+str1)){
                int sz  = gcd(n1,n2);
                return str1.substring(0,sz);
            }
            return "";
        }
    }
    static int gcd(int n1, int n2){
        if(n1 == 1 || n2 == 1)
            return 1;
        if (n1 < n2)
            return gcd(n2, n1);
        if(n1%n2 == 0)
            return n2;
        else
            return gcd(n2, n1%n2);
    }

    public static List<Boolean> kidsWithCandies(int[] candies, int extraCandies) {
        int n = candies.length;
        if(n == 0)
            return Collections.emptyList();
        int maxVal = Arrays.stream(candies).max().getAsInt();
        Boolean[] ans = new Boolean[n];
        for(int i=0;i<n;i++){
            ans[i] = candies[i] >= maxVal;
        }
        return Arrays.asList(ans);
    }


}

