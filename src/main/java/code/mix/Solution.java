package code.mix;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Solution {
    private static int cnt;
//    private final int  cnt2;
    private int cnt3;
    public static void main(String[] args) throws IOException {
        try{
            System.out.println("xx");
            m();
        }catch (RuntimeException ex){
            System.out.println("ex");
        }finally {
            System.out.println("finally");
        }

        try {
            Solution s =  new Solution();
            s.m2();
        }catch (Exception e){
            System.out.println(e.getCause());
        }
        finally {
            System.out.println("finally2");
        }
    }
    public static void m(){
        int x;
//        System.out.println(x);
        System.out.println(cnt);
//        System.out.println(cnt2);
    }

    public void m2(){
        System.out.println(cnt3);
    }

    public <T> List<T> fromArrayToList(T[] a) {
        return Arrays.stream(a).collect(Collectors.toList());
    }
}

class Test2{
    public static void main(String[] args) {
        System.out.println(encrypt("okffng-Owvb", 2));
    }

    public static String encrypt(String s, int k){
        char[] chars = s.toCharArray();
        for (int i = 0; i < s.length(); i++) {
            System.out.println((int) chars[i]);
            if((chars[i] >=97 && chars[i] <=122)|| (chars[i] >=65 && chars[i] <=90 )) {
                int ascii = chars[i] + k;
                if(ascii < 65 || (ascii >90 && ascii < 97) || ascii > 122)
                    ascii -= 26;
                chars[i] = (char) ascii;
            }
        }
        return String.valueOf(chars);
    }
}
