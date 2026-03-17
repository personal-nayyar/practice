package code.tree;

import org.apache.hadoop.shaded.org.apache.kerby.kerberos.kerb.type.ad.AndOr;

import java.util.ArrayList;
import java.util.List;

/**
 Branch Sum in Binary Tree — Competitive Programming with ...
 A branch sum in a binary tree is the total sum of node values along a path from the root node to any leaf node.
 A leaf node is defined as a node with no children.
 Definition: The sum of node values starting from the root and ending at a leaf node.
 Path Definition: A path is a sequence of connected nodes where each node appears at most once.
 Leaf Node: A node with no left or right children.
 Traversal: Typically solved using Depth-First Search (DFS) to traverse down each branch.
 * */
public class BranchSum {

    static class TreeNode{
        int val;
        TreeNode left, right;

        TreeNode(int val){
            this.val =  val;
        }
    }

    public static List<Integer> branchSum(TreeNode root){
        List<Integer> result  = new ArrayList<>();
        dfs(root, 0, result);
        return result;
    }

    public static void dfs(TreeNode node, int currSum, List<Integer> result){
        if (node == null)
            return;
        currSum += node.val;

        // if leaf
        if (node.left == null && node.right == null)
            result.add(currSum);

        // left subtree
        dfs(node.left, currSum, result);

        // right subtree
        dfs(node.right, currSum, result);
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        root.left.left.left = new TreeNode(8);
        root.left.left.right = new TreeNode(9);
        root.left.right.left = new TreeNode(10);
        root.left.right.right = new TreeNode(11);
        root.right.left.left = new TreeNode(12);
        root.right.left.right = new TreeNode(13);
        root.right.right.left = new TreeNode(14);
        root.right.right.right = new TreeNode(15);

        List<Integer> result = branchSum(root);
        System.out.println(result);

        // Visual representation of the tree
        System.out.println("                    1");
        System.out.println("                  /   \\");
        System.out.println("                 2     3");
        System.out.println("               / \\   / \\");
        System.out.println("             4   5  6   7");
        System.out.println("            / \\ / \\ / \\");
        System.out.println("          8   9 10 11 12");
        System.out.println("         / \\ / \\ / \\ / \\");
        System.out.println("       13 14 15   ...  ");
    }
}
