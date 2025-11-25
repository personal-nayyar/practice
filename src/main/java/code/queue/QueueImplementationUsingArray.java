package code.queue;

interface Queue{
    void enqueue(int data);
    int dequeue();
    int peek();
    boolean isEmpty();
    boolean isFull();
    int size();
    void display();
}
public class QueueImplementationUsingArray implements Queue{
    int[] arr;
    int front;
    int rear;
    int size;

    public QueueImplementationUsingArray(int size){
        this.size = size;
        arr = new int[size];
        front = 0;
        rear = -1;
    }

    public void enqueue(int data){
        if(rear == size-1){
            System.out.println("Queue is full");
            throw new RuntimeException("Queue is full");
        }
        arr[++rear] = data;
    }

    public int dequeue(){
        if(front > rear){
            System.out.println("Queue is empty");
            throw new RuntimeException("Queue is empty");
        }
        return arr[front++];
    }

    public int peek(){
        if(front > rear){
            System.out.println("Queue is empty");
        }
        return arr[front];
    }

    public boolean isEmpty(){
        return front > rear;
    }

    public boolean isFull(){
        return rear == size-1;
    }

    public int size(){
        return rear - front + 1;
    }

    public void display(){
        for (int i = front; i <= rear; i++) {
            System.out.print(arr[i]+" ");
        }
        System.out.println();
    }
}

class Runner{
    public static void main(String[] args) {
        Queue q = new QueueImplementationUsingArray(5);

        q.display();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);

        q.display();

        System.out.println("Dequeued element: " + q.dequeue());
        System.out.println("Dequeued element: " + q.dequeue());

        q.display();

        q.enqueue(6);

        q.display();

        System.out.println("Peeked element: " + q.peek());

        q.display();

        System.out.println("Queue size: " + q.size());

        System.out.println("Queue is empty: " + q.isEmpty());

        System.out.println("Queue is full: " + q.isFull());
    }
}
