package DSimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//    Q: sort odd digit numbers based on their middle digit
//    {"10201","30213","90051","36103","92315"}
public class SortByMiddleDigit {
    public static void main(String[] args) {
        List<String> numbers = new ArrayList();
        numbers.add("10201");
        numbers.add("30213");
        numbers.add("90051");
        numbers.add("36103");
        numbers.add("92315");
        SortByMiddleDigit sortByMiddleDigit = new SortByMiddleDigit();
        sortByMiddleDigit.sortBasedOnMiddleDigit(numbers);
        System.out.println(numbers);
    }
    public void sortBasedOnMiddleDigit(List<String> numbers) {
        Collections.sort(numbers, new SortByMiddleDigitComparator());
    }

    class SortByMiddleDigitComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            int mid =  o1.length()/2;
            int mid2 = o2.length()/2;
            return o1.charAt(mid) - o2.charAt(mid2) == 0 ? o1.compareTo(o2) : o1.charAt(mid) - o2.charAt(mid2);
        }
    }

}
