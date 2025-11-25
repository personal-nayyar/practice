package companies.Arrise;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
* Let's implement a stack which contains methods push(), pop() of a stack
and perform operations as below example:

freqStack.push(5); // The stack is [5]
freqStack.push(7); // The stack is [5,7]
freqStack.push(5); // The stack is [5,7,5]
freqStack.push(7); // The stack is [5,7,5,7]
freqStack.push(4); // The stack is [5,7,5,7,4]
freqStack.push(5); // The stack is [5,7,5,7,4,5]
freqStack.pop();   // return 5, as 5 is the most frequent. The stack becomes [5,7,5,7,4].
freqStack.pop();   // return 7, as 5 and 7 is the most frequent, but 7 is closest to the top. The stack becomes [5,7,5,4].
freqStack.pop();   // return 5, as 5 is the most frequent. The stack becomes [5,7,4].
freqStack.pop();   // return 4, as 4, 5 and 7 is the most frequent, but 4 is closest to the top. The stack becomes [5,7].
*/
public class FreqStack {
    static Map<Integer, Integer> freqMap;
    static Map<Integer, Stack<Integer>> freqStackMap;
    static int maxFreq;
    public FreqStack() {
        freqMap = new HashMap<>();
        freqStackMap = new HashMap<>();
        maxFreq = 0;
    }

    public static void main(String[] args) {
        FreqStack freqStack = new FreqStack();
        freqStack.push(5);
        freqStack.push(7);
        freqStack.push(5);
        freqStack.push(7);
        freqStack.push(4);
        freqStack.push(5);
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
        System.out.println(freqStack.pop());
    }

    public void push(int x) {
        freqMap.put(x, freqMap.getOrDefault(x, 0) + 1);
        maxFreq = Math.max(maxFreq, freqMap.get(x));
        freqStackMap.computeIfAbsent(freqMap.get(x), k -> new Stack<>()).push(x);
    }

    public int pop() {
        int x = freqStackMap.get(maxFreq).pop();
        freqMap.put(x, freqMap.get(x) - 1);
        if (freqStackMap.get(maxFreq).isEmpty()) {
            maxFreq--;
        }
        return x;
    }
}
