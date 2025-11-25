package code.stack;

interface Stack{
    void push(int data);
    int pop();
    int peek();
    boolean isEmpty();
    boolean isFull();
    int size();
    void display();
}
public class StackImplementationUsingArray implements Stack{
    int[] arr;
    int top;
    int size;

    public StackImplementationUsingArray(int size){
        this.size = size;
        arr = new int[size];
        top = -1;
    }

    public void push(int data){
        if(top == size-1){
            System.out.println("Stack is full");
            throw new RuntimeException("Stack is full");
        }
        arr[++top] = data;
    }

    public int pop(){
        if(top == -1){
            System.out.println("Stack is empty");
            throw new RuntimeException("Stack is empty");
        }
        return arr[top--];
    }

    public int peek(){
        if(top == -1){
            System.out.println("Stack is empty");
            throw new RuntimeException("Stack is empty");
        }
        return arr[top];
    }

    public boolean isEmpty(){
        return top == -1;
    }

    public boolean isFull(){
        return top == size-1;
    }

    public int size(){
        return top+1;
    }

    public void display(){
        for (int i = 0; i <= top; i++) {
            System.out.print(arr[i]+" ");
        }
        System.out.println();
    }
}

class Runner{
    public static void main(String[] args) {
        Stack stack = new StackImplementationUsingArray(5);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);
        stack.display();
        System.out.println("Popped element: " + stack.pop());
        stack.display();
        System.out.println("Popped element: " + stack.pop());
        stack.display();
        System.out.println("Size: " + stack.size());
        System.out.println("Is empty: " + stack.isEmpty());
        System.out.println("Peeked element: " + stack.peek());
        stack.display();
        System.out.println("Is full: " + stack.isFull());
    }
}
