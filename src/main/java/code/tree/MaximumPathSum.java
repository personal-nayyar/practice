package code.tree;
/**
 🟢 Problem: Maximum Path Sum in a Binary Tree

 You are given the root of a binary tree where each node contains an integer value (which may be positive, negative, or zero).

 A path is defined as any sequence of nodes connected by parent-child relationships. The path:
 •	Can start at any node
 •	Can end at any node
 •	Must follow parent-child connections
 •	Must contain at least one node
 •	Does not need to pass through the root

 Your task is to return the maximum possible sum of the values along any valid path in the tree.
 5
 / \
 3   8
 / \    \
 2   4     10
 / \
 13  7
 Some possible paths:
 •	13 → 4 → 7
 •	2 → 3 → 5 → 8 → 10
 •	3 → 4 → 13
 •	10
 •	7
 The output should be the maximum sum among all such path
 * */
public class MaximumPathSum {
    public static int maxPathSum(TreeNode root){
        /*
         at each node ask
            -> ask left subtree what is the max it can provide : leftMax
            -> ask right subtree what is the max it can provide : rightMax
            -> currPath sum -> node.val+leftMax+rightMax and check if it max
            -> return to parent node node.val+max(leftMax, rightMax)
        * */
        return dfs(root, 0);
    }

    public static int dfs(TreeNode node, int maxSum){
        if (node == null)
            return 0;
        int leftMax = dfs(node.left, maxSum);
        int rightMax = dfs(node.right, maxSum);

        leftMax = Math.max(0, leftMax);
        rightMax = Math.max(0, rightMax);

        int currPathSum = node.data + leftMax+rightMax;
        maxSum =  Math.max(maxSum, currPathSum);

        return node.data + Math.max(leftMax, rightMax);
    }
}
