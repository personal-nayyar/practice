package leetcode.linkedIn;

import lombok.Builder;

@Builder
public class ListNode {
      int val;
      ListNode next;
      ListNode() {}
      ListNode(int val) { this.val = val; }
      ListNode(int val, ListNode next) { this.val = val; this.next = next; }

    public static void print(ListNode head){
          ListNode curr =  head;
          while (curr != null){
              System.out.print(curr.val +" -->");
              curr = curr.next;
          }
        System.out.println("");
    }
}
