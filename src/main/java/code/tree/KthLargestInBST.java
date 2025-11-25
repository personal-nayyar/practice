package code.tree;

import java.util.ArrayList;
import java.util.List;

public class KthLargestInBST {
    static  int count = 0;
    static int result2  = -1;
    static List<Integer> result = new ArrayList<>();
    public static void main(String[] args) {
        Node root = new Node(5);
        root.left = new Node(3);
        root.right = new Node(7);

        root.left.left = new Node(2);
        root.left.right = new Node(4);
        root.right.right = new Node(8);
        KthLargestInBST.kthLargest(root, 2);
        System.out.println("result2: "+result2);
//        System.out.println(KthLargestInBST.kthLargestInBST(root, 1, new ArrayList<>()));
//        System.out.println(KthLargestInBST.kthLargestInBST(root, 3, new ArrayList<>()));
//        reverseInorder(root);

    }

    // Inorder traversal of the tree [Left->Root->Right] -> sorted Ascending order
    public static void inorderTraversal(Node root){
        if (root == null){
            return;
        }
        inorderTraversal(root.left);
        System.out.print(root.data + " ");
        inorderTraversal(root.right);
    }

    // reverse inorder traversal of the tree [Right->Root->Left] -> sorted Descending order
    public static void reverseInorder(Node root) {
        if (root == null) {
            return;
        }
        reverseInorder(root.right);
        System.out.print(root.data + " ");
        reverseInorder(root.left);
    }

    public static void reverseInorder(Node root, List<Integer> result){
        if (root == null){
            return;
        }
        reverseInorder(root.right, result);
        result.add(root.data);
        reverseInorder(root.left,result);
    }

    public static boolean kthLargest(Node root, int k) {
        if (root == null) return false;

        // Step 1: Go right first (larger values)
        if (kthLargest(root.right, k)) return true;

        // Step 2: Process current node
        count++;
//        System.out.println("count:"+count);
        if (count == k) {
            result2 = root.data;
            return true; // stop recursion once found
        }

        // Step 3: Go left only if not found
        return kthLargest(root.left, k);
    }

}
