package code.tree;

//https://leetcode.com/problems/delete-leaves-with-a-given-value/description/?utm_source=chatgpt.com
/*
Given a binary tree root and an integer target, delete all the leaf nodes with value target.



       //                 1

       //        1              2

       //   3      4              3

       // 3   3



       // target: 4



       //                 1

       //        1              2

       //   3                   3

       // 3   3



       // target: 3



       //                1

       //        1              2

 */

public class RemoveTargetLeafNode {
    public static void main(String[] args) {

    }

    static TreeNode recursiveRemoveLeafNode(TreeNode root, int target){
        // base condition
        if (root == null)
            return null;

        TreeNode leftTree = recursiveRemoveLeafNode(root.left, target);

        TreeNode rightTree = recursiveRemoveLeafNode(root.right, target);

        // if both left and right are null -> leaf
        boolean isLeaf = leftTree == null && rightTree == null;

        // leaf and match to target
        if (isLeaf && root.data == target){
            return null; // remove this node
        }
        return root;
    }
}
