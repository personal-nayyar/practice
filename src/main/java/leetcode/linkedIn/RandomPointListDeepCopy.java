package leetcode.linkedIn;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

public class RandomPointListDeepCopy {

    @Setter
    @Getter
    static class Node{
        int val;
        Node next;
        Node random;

        public Node(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }

        public String toString(){
            return "[" + this.val + ", "+this.random+"]";
        }

        public static void print(Node head){
            Node curr = head;
            while (curr != null){
                System.out.print(curr);
                curr = curr.next;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        Node n7 = new Node(7);
        Node n13 = new Node(13);
        Node n11 = new Node(11);
        Node n10 = new Node(10);
        Node n1 = new Node(1);

        n7.next = n13;
        n7.random = null;

        n13.next = n11;
        n13.random = n7;

        n11.next = n10;
        n11.random = n1;

        n10.next = n1;
        n10.random = n11;

        n1.next = null;
        n1.random = n7;

        Node.print(n7);
        Node newHead = deepCopy(n7);
        Node.print(newHead);

    }

    public static Node deepCopy(Node head){
        // base condition
        if (head == null)
            return null;
        Map<Node, Node> map = new HashMap<>();

        Node curr =  head;
        while(curr != null){
            map.put(curr, new Node(curr.val));
            curr =  curr.next;
        }

        curr = head;
        while (curr != null){
            Node copy =  map.get(curr);
            copy.next = map.get(curr.next);
            copy.random = map.get(curr.random);
            curr = curr.next;
        }
        return map.get(head);
    }

    public static Node deepCopyConstantSpace(Node head){
        // base condition
        if (head == null)
            return null;

        // step 1: create interleave node
        // A ->B -> C
        // A ->A` -> B -> B` -> C -> C`
        Node curr = head;
        while (curr != null){
            Node copy = new Node(curr.val);
            copy.next = curr.next;
            curr.next = copy;
            curr = copy.next;
        }

        // set random pointer
        curr =  head;
        while (curr != null){
            Node copy = curr.next;
            if (curr.random != null){
                copy.random = curr.next.random;
            }
            curr = copy.next;
        }

        // separate both list
        curr = head;
        Node newHead = head.next;
        while (curr != null){
            Node copy = curr.next;
            curr.next = copy.next; // restore original

            if (copy.next != null){
                copy.next = copy.next.next;
            }
            curr = curr.next;
        }
        return newHead;
    }
}
