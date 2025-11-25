package code.linkedList;


import java.util.List;

class ListNode<T>{
    T data;
    ListNode<T> next;

    ListNode(T data){
        this.data =  data;
    }

    ListNode(T data, ListNode<T> next){
        this.data =  data;
        this.next = next;
    }
}

public class LinkedList<T> {
    ListNode<T> head;

    LinkedList(ListNode<T> head){
        this.head =  head;
    }


    public void add(T data){
        if(head == null){
            head = new ListNode<T>(data);
            return;
        }
        ListNode<T> temp = head;
        while(temp.next != null){
            temp = temp.next;
        }
        temp.next = new ListNode<T>(data);
    }

    static <T> void display(ListNode<T> head){
        ListNode<T> temp = head;
        while (temp != null) {
            System.out.print(temp.data + " -> ");
            temp = temp.next;
        }
        System.out.println("null");
    }


    static <T extends Comparable<T>> void sort(ListNode<T> head) {
        ListNode<T> temp = head;
        ListNode<T> curr = null;
        ListNode<T> next = null;
        while (temp != null && temp.next != null) {
            curr = head;
            while (curr != temp) {
                next = curr.next;
                if (curr.data.compareTo(next.data) > 0) {
                    T tempData = curr.data;
                    curr.data = next.data;
                    next.data = tempData;
                }
                curr = curr.next;
            }
            temp = temp.next;
        }
    }


    static <T> ListNode<T> getNode(ListNode<T> head, T key){
        ListNode<T> temp = head;
        while (temp != null && !temp.data.equals(key)) {
            temp = temp.next;
        }
        return temp;
    }
}
