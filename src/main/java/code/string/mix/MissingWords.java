package code.string.mix;


import java.util.*;
/*
For example:
Input
String 1 = "I love programming, pizza, coke and chips";
String 2 = "I programming, chips";
Required Output
Missing words = "love pizza coke and";
*/
public class MissingWords {
    public static void main(String[] args) {
        System.out.println(missingWords("I love programming, pizza, coke and chips","I programming, chips"));
        System.out.println(missingWordsInBuild("I love programming, pizza, coke and chips","I programming, chips"));
        System.out.println(missingWordsOptimised("I love programming, pizza, coke and chips","I programming, chips"));
    }

    public static List<String> missingWords(String s1, String s2){   // O(n*m)
        List<String> l1 = Arrays.asList(s1.replaceAll("[,g.;-]","").split("\\s"));
        List<String> l2 = Arrays.asList(s2.replaceAll("[,g.;-]","").split("\\s"));
        List<String> missing = new ArrayList<>();

        for (String str: l1) {
            if (!l2.contains(str))
                missing.add(str);
        }
        return missing;
    }

    public static List<String> missingWordsInBuild(String s1, String s2){ // O(n*m)
        List<String> l1 = new ArrayList<>(Arrays.asList(s1.replaceAll("[,.;-]","").split("\\s")));
        List<String> l2 = new ArrayList<>(Arrays.asList(s2.replaceAll("[,.;-]","").split("\\s")));
        l1.removeAll(l2);
        return l1;
    }

    public static List<String> missingWordsOptimised(String s1, String s2){
        List<String> l1 = Arrays.asList(s1.replaceAll("[,.;-]","").split("\\s"));
        List<String> l2 = Arrays.asList(s2.replaceAll("[,.;-]","").split("\\s"));
        if(l1.size() < l2.size()){
            List<String> temp =  l1;
            l1 = l2;
            l2 = temp;
        }
        List<String> missing = new ArrayList<>();

        int i=0,j=0;
        while(i<l1.size() && j< l2.size()){
            if (l1.get(i).equalsIgnoreCase(l2.get(j))){
                i++;j++;
            }else {
                missing.add(l1.get(i));
                i++;
            }
        }
        while(i< l1.size()){
            missing.add(l1.get(i));
        }
        return missing;
    }
}
