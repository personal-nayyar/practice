package code.linkedList;

// https://leetcode.com/problems/intersection-of-two-linked-lists/description/

import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class CheckLinkedListsIntersection {
    public static void main(String[] args) {
        LinkedList<Integer> listA = new LinkedList<>(new ListNode<>(1));
        listA.add(2);
        listA.add(3);

        LinkedList<Integer> listB = new LinkedList<>(new ListNode<>(2));
        listB.add(4);
        listB.add(5);
        ListNode<Integer> intersectionNode = getIntersectionNode(listA.head, listB.head);
        System.out.println(intersectionNode == null ? "No intersection" : intersectionNode.data);


        LinkedList<Integer> listA1 = new LinkedList<>(new ListNode<>(1));
        listA1.add(2);
        listA1.add(3);

        LinkedList<Integer> listB1 = new LinkedList<>(new ListNode<>(4));
        listB1.add(5);
        ListNode<Integer> intersectionNode1 = getIntersectionNode(listA1.head, listB1.head);
        System.out.println(intersectionNode1 == null ? "No intersection" : intersectionNode1.data);

        LinkedList<Integer> listA2 = new LinkedList<>(new ListNode<>(1));
        listA2.add(2);

        LinkedList<Integer> listB2 = new LinkedList<>(new ListNode<>(2));
        listB2.add(3);

        ListNode<Integer> intersectionNode2 = getIntersectionNode(listA2.head, listB2.head);
        System.out.println(intersectionNode2 == null ? "No intersection" : intersectionNode2.data);
    }
    public static ListNode<Integer> getIntersectionNode(ListNode<Integer> headA, ListNode<Integer> headB) {
        LinkedList.sort(headA);
        LinkedList.sort(headB);
        while(headA != null && headB != null){
            if(Objects.equals(headA.data, headB.data)){
                return headA;
            } else if (headA.data < headB.data){
                headA = headA.next;
            }
            else
                headB = headB.next;
        }
        return null;
    }
}
