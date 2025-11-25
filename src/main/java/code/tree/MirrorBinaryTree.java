package code.tree;

import java.util.LinkedList;
import java.util.Queue;

public class MirrorBinaryTree {
    public static void main(String[] args) {

    }

    public TreeNode invertTreeRecursive(TreeNode root){
        if (root == null){
            return null;
        }
        // swap left and right children
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;

        invertTreeRecursive(root.left);
        invertTreeRecursive(root.right);
        return root;
    }

    public TreeNode invertTreeIterative(TreeNode root){
        if (root == null){
            return null;
        }
        Queue<TreeNode> queue = new LinkedList<>(); // linkedList as Queue
        queue.add(root);

        while (!queue.isEmpty()){
            TreeNode node = queue.poll();
            // swap left and right children
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;

            // add left and right children to queue
            if (node.left != null){
                queue.add(node.left);
            }
            if (node.right != null){
                queue.add(node.right);
            }
        }
        return root;
    }
}
