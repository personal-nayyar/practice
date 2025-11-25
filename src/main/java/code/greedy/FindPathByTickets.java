package code.greedy;
import java.util.*;

// Q: find path by tickets
// {"del'}

public class FindPathByTickets {
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>(){{
            put("hyd","mum");
            put("del", "jai");
            put("mum","nag");
            put("nag", "del");
            put("chen","hyd");
        }};
        System.out.println(findPath(map));
    }

    public static String findPath(Map<String, String> paths){
        Set<String> sources =  paths.keySet();
        Set<String> dests =  new HashSet<>(paths.values());
        String start = "";
        for (String source: sources) {
            if (!dests.contains(source)){
                start = source;
                break;
            }
        }
        String path = "";
        while(paths.containsKey(start)){
            path += start + "-->";
            start = paths.get(start);
        }
        path += start;
        return path;
    }
}
