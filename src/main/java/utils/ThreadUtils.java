package utils;

public class ThreadUtils {
    public static void sleepSeconds(int seconds){
        try {
            Thread.sleep(seconds * 1000);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
