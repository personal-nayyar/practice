package leetcode.linkedIn;

import utils.DSAUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeKList {
    public static void main(String[] args) {
        ListNode head1 = new ListNode(1);
        head1.next =  new ListNode(4);
        head1.next.next = new ListNode(5);

        ListNode head2 = new ListNode(1);
        head2.next =  new ListNode(3);
        head2.next.next = new ListNode(4);

//        ListNode.print(head1);
//        ListNode.print(head2);
//        ListNode.print(merge(head1, head2));


        ListNode head3 = new ListNode(2);
        head3.next =  new ListNode(6);

        List list = Arrays.asList(head1, head2, head3);

        ListNode.print(head1);
        System.out.println("");
        ListNode.print(head2);
        ListNode.print(mergeKList(list));

    }

    public static ListNode mergeKList(List<ListNode> list){
        // divide and merge
        return mergeList(list, 0, list.size()-1);
    }

    public static ListNode mergeList(List<ListNode> list, int left, int right){
        // base case
        if (left == right)
                return list.get(left);

        int mid = left+(right-left)/2;
        ListNode leftList = mergeList(list, left, mid);

        ListNode rightList = mergeList(list, mid+1, right);

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
