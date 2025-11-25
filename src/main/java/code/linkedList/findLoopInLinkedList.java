package code.linkedList;

public class findLoopInLinkedList {
    public static void main(String[] args) {

        ListNode<Integer> listNode = new ListNode<>(1);
        LinkedList<Integer> listA = new LinkedList<>(listNode);
        listA.add(2);
        listA.add(3);
        listA.add(4);
        listA.add(5);
        listA.getNode(listNode, 4).next = listA.getNode(listNode, 3);

        System.out.println(findLoop(listA.head).data);
    }

    static ListNode<Integer> findLoop(ListNode<Integer> head){
        ListNode<Integer> slow = head;
        ListNode<Integer> fast = head;
        while(fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            if(slow == fast){
                ListNode<Integer> temp = head;
                while(temp != slow){
                    temp = temp.next;
                    slow = slow.next;
                }
                return temp;
            }
        }
        return null;
    }
}
