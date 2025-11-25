package code.string.mix;
import java.util.ArrayList;
import java.util.List;

/**
 * validateion:
 *  - 4 segment should be there
 *  -  0 <= ip <=255
 *  - not trelling 0 as 1.01.1.1 -> not valid, 10.1.1.1 -> valid
 */

public class GenerateValidIps {

    public static void main(String[] args) {
        System.out.println(generateValidIps("25505011535"));
    }

    public static List<String> generateValidIps(String str){ // O(n^3)
        int n  = str.length();
        List<String> list =  new ArrayList<>();
        // first validation for a valid ip
        if(n < 4 || n > 12){ // base condition
            return list;
        }
        for (int i = 0; i < n-3; i++) {  // n-3 because need 3 more seg that can only if i left 3 element rem
            for (int j = i+1; j < n-2; j++) {
                for (int k = j+1; k < n-1; k++) {
                    String ip = str.substring(0, i+1)+"."+
                            str.substring(i+1, j+1)+"."+
                            str.substring(j+1,k+1)+"."+
                            str.substring(k+1, n);
                    if(validaIp(ip)){
                        list.add(ip);
                    }
                }
            }
        }
        return list;
    }

    public static boolean validaIp(String str){ // O(n)
        String[] segs = str.split("\\.");
        boolean status =  true;
        for (String seg: segs) {
            if (seg.length() > 3){
                status = false;
                break;
            }
            if(0 > Integer.parseInt(seg)  || Integer.parseInt(seg) > 255){
                status =  false;
                break;
            }
            if(seg.length() > 1 && seg.charAt(0) == '0'){
                status = false;
                break;
            }
        }
        return status;
    }
}
