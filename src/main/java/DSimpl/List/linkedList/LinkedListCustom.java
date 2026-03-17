package DSimpl.List.linkedList;

public interface LinkedListCustom {
    void addAtBeginning(String val);
    void addAtEnd(String val);
    void insert(int pos, String val);
}

class LinkedListCustomImpl implements LinkedListCustom{

    static class Node{
        String val;
        Node next;

        Node(String val){
            this.val = val;
        }
    }

    private Node head;

    LinkedListCustomImpl(){}

    @Override
    public void addAtBeginning(String val) {
        Node node = new Node(val);
        if (head == null)
            head = node;
        else{
            node.next =  head;
            head =  node;
        }
    }

    @Override
    public void addAtEnd(String val) {
        Node node = new Node(val);
        if (head == null)
            head = node;
        else{
            Node curr =  head;
            while(curr.next != null)
                curr = curr.next;
            curr.next = node;
        }
    }

    @Override
    public void insert(int pos, String val) {
        if (pos == 0)
            addAtBeginning(val);
        Node node  = new Node(val);
        Node prev = null, curr=  head;
        int i = 0;
        while (curr != null && i == pos){
            curr = curr.next;
            i++;
            prev = curr;
        }
        prev.next = node;
        node.next =  curr;
    }
}
