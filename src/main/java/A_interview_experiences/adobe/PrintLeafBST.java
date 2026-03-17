package A_interview_experiences.adobe;

import org.antlr.v4.runtime.tree.Tree;

public class PrintLeafBST {
    public static void main(String[] args) {
        TreeNode root = new TreeNode(10);
        root.left = new TreeNode(5);
        root.right = new TreeNode(15);
        root.left.left = new TreeNode(3);
        root.left.right = new TreeNode(7);
        root.right.right = new TreeNode(20);
        printLeaf(root);
        System.out.println("\n\n\n");

        root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        printLeaf(root);
        System.out.println("\n\n\n");


        root = new TreeNode(1);
        printLeaf(root);
        System.out.println("\n\n\n");

    }


    static class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val){
            this.val =  val;
        }
    }
    /*
        10
       /  \
      5    15
     / \     \
    3   7     20
    ans: 3, 7, 20
    * */
    public static void printLeaf(TreeNode root){
        if (root == null)
            return;
        // check is leaf?
        if (root.left == null && root.right == null){
            System.out.print(root.val +" ");
            return;
        }
        printLeaf(root.left);
        printLeaf(root.right);
    }

}
