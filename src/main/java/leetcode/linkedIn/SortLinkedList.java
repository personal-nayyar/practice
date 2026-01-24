package leetcode.linkedIn;

import java.util.List;

import static leetcode.linkedIn.MergeKList.mergeList;

public class SortLinkedList {

    public static void main(String[] args) {
        ListNode head = new ListNode(4);
        head.next = new ListNode(2);
        head.next.next = new ListNode(1);
        head.next.next.next = new ListNode(3);

        ListNode.print(sortList(head));
    }


    public static ListNode sortList(ListNode head){
        // base case
        if(head == null || head.next == null)
            return head;
        ListNode slow = head, fast =head, prev= null;
        while(fast !=null && fast.next != null){
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        prev.next =null; // break into two half
        ListNode leftList = sortList(head);

        ListNode rightList = sortList(slow);

        return merge(leftList, rightList);
    }


    public static ListNode merge(ListNode list1, ListNode list2){
        ListNode merged = new ListNode(0); // simplify creation
        ListNode curr = merged;
        while (list1 != null && list2 != null){
            if (list1.val < list2.val){
                curr.next = list1;
                list1 = list1.next;
            }else {
                curr.next = list2;
                list2 = list2.next;
            }
            curr = curr.next;
        }

        curr.next = list1 == null ? list2: list1;
        return merged.next;
    }
}
