package code.linkedList;

import java.util.Collections;
import java.util.List;

public class ReverseLinkedList {


    public static void main(String[] args) {
        LinkedList<Integer> list = new LinkedList<>(new ListNode<>(1));
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);

        System.out.println("Original LinkedList: ");
        LinkedList.display(list.head);
        // Reverse the linked list
        ListNode<Integer> reversedHead = reverse(list.head);
        
        // Print the reversed list
        System.out.print("Reversed LinkedList: ");
        LinkedList.display(reversedHead);
    }

    static <T> ListNode<T> reverse(ListNode<T> head){
        ListNode<T> prev = null, temp = null, curr = head;
        while(curr != null){
            temp = curr.next;
            curr.next = prev;
            prev = curr;
            curr = temp;
        }
        return prev;
    }

    static <T> List<T> reverseUsingBuiltInFunction(List<T> list){
        Collections.reverse(list);
        return list;
    }
}
