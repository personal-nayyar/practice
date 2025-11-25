package syncronization;

public class DeadLockTest {
    public static void main(String[] args) {
        final String resource1 = "resource_1";
        final String resource2 = "resource_2";

        Runnable t1 =  new Runnable() {
            @Override
            public void run() {
                synchronized (resource1){
                    System.out.println("t1 acquired lock on resource1");
                }
                try {
                    Thread.sleep(2000);
                }catch (Exception e){
                    System.out.println(e);
                }
                synchronized (resource2){
                    System.out.println("t1 acquired lock on resource2");
                }
            }
        };

        Runnable t2 =  new Runnable() {
            @Override
            public void run() {
                synchronized (resource2){
                    System.out.println("t2 acquired lock on resource2");
                }
                try {
                    Thread.sleep(5000);
                }catch (Exception e){
                    System.out.println(e);
                }
                synchronized (resource1){
                    System.out.println("t2 acquired lock on resource1");
                }
            }
        };
        new Thread(t1).start();
        new Thread(t2).start();
    }
}
