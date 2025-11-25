package DSimpl;

public interface LinkedListCustom<T> {
    void add(T data);
    void remove(T data);
    void print();
    void reverse();
}

class CustomLinkedListImpl<T> implements LinkedListCustom<T> {

    static class Node<T> {
        T data;
        Node next;
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }

    Node head;
    int size;

    public CustomLinkedListImpl() {
        this.head = null;
        this.size = 0;
    }

    @Override
    public void add(T data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    @Override
    public void remove(T data) {
        if (head == null) {
            return;
        }
        if (head.data.equals(data)) {
            head = head.next;
            size--;
            return;
        }
        Node current = head;
        while (current.next != null && !current.next.data.equals(data)) {
            current = current.next;
        }
        if (current.next != null) {
            current.next = current.next.next;
            size--;
        }
    }

    @Override
    public void print() {
        Node current = head;
        while (current != null) {
            System.out.print(current.data + " ");
            current = current.next;
        }
        System.out.println();
    }

    @Override
    public void reverse() {
        Node prev = null;
        Node current = head;
        while (current != null) {
            Node next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        head = prev;
    }

    public int size() {
        return size;
    }
}

