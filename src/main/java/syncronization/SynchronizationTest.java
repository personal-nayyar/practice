package syncronization;

public class SynchronizationTest {
    public static void main(String[] args) {
        SharedResource resource =  new SharedResource(); // single shared object
        Task1 t1 =  new Task1(resource);
        Task2 t2 =  new Task2(resource);
        t1.start();
        t2.start();
//        t1.start();
    }

    public static void anonymousClassCall(String[] args){
        SharedResource resource =  new SharedResource();
        Thread t1 = new Thread(){
            @Override
            public void run(){
                resource.synchronizedMethod(1);
            }
        };

        Thread t2 =  new Thread(){
            @Override
            public void run(){
                resource.synchronizedMethod(10);
            }
        };

        t1.start();
        t2.start();
    }
}

class SharedResource{
    public synchronized void  synchronizedMethod(int threadId){
        for (int i = 1; i <= 5; i++) {
            System.out.println(threadId * i);
            try {
                Thread.sleep(1000);
            }catch (Exception e){
                System.out.println(e);
            }
        }
    }

    public void synchronizedBlock(int n){
        synchronized (this){
            for (int i = 1; i <= 5; i++) {
                System.out.println(n*i);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        }
    }

    public void staticSynchronized(int n){
        synchronized (SharedResource.class){
            for (int i = 1; i <= 5; i++) {
                System.out.println(n*i);
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        }
    }
}

class Task1 extends  Thread{
    SharedResource resource;
    Task1(SharedResource resource){
        this.resource =  resource;
    }

    @Override
    public void run(){
        resource.synchronizedMethod(1);
    }
}

class Task2 extends Thread{
    SharedResource resource;
    Task2(SharedResource resource){
        this.resource =  resource;
    }

    @Override
    public void run(){
        resource.synchronizedMethod(10);
    }
}