package DSimpl;

public interface DoublyLinkedListCustom {
    void add(int data);
    void remove(int data);
    void print();
    void reverse();
}

class CustomDoublyLinkedListImpl implements DoublyLinkedListCustom {
    static class Node {
        int data;
        Node next;
        Node prev;
        Node(int data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    Node head;
    Node tail;
    int size;

    public CustomDoublyLinkedListImpl() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    @Override
    public void add(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    @Override
    public void remove(int data) {
        if (head == null) {
            return;
        }
        if (head.data == data) {
            head = head.next;
            if (head != null) {
                head.prev = null;
            }
            size--;
            return;
        }
        Node current = head;
        while (current.next != null && current.next.data != data) {
            current = current.next;
        }
        if (current.next != null) {
            current.next = current.next.next;
            if (current.next != null) {
                current.next.prev = current;
            }
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
            current.prev = next;
            prev = current;
            current = next;
        }
        head = prev;
    }

    public int size() {
        return size;
    }
}

