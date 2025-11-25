package code.tree;

import java.util.*;

/**
 * https://leetcode.com/problems/all-nodes-distance-k-in-binary-tree/description/
 * */
public class DistanceKFromTarget {
    /*
    find the target node in binary tree : complexity O(n)
    find all nodes at distance k from target node : complexity O(n)
    * */

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        TreeNode target = root.left; // Node 5
        int k = 2;
        List<TreeNode> result = collectDown(target, k, new ArrayList<>());
        System.out.println(result);
    }

    public List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, List<TreeNode>> graph = new HashMap<>();
        buildGraph(root, null, graph);

        List<Integer> result = new ArrayList<>();
        Set<TreeNode> visited = new HashSet<>();
        Queue<TreeNode> queue = new LinkedList<>();

        queue.offer(target);
        visited.add(target);
        int distance = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            if (distance == k) {
                for (TreeNode node : queue) {
                    result.add(node.data);
                }
                return result;
            }
            for (int i = 0; i < size; i++) {
                TreeNode current = queue.poll();
                for (TreeNode neighbor : graph.getOrDefault(current, new ArrayList<>())) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        queue.offer(neighbor);
                    }
                }
            }
            distance++;
        }
        return result;
    }

    private static void buildGraph(TreeNode node, TreeNode parent, Map<TreeNode, List<TreeNode>> graph) {
        if (node == null) return;

        graph.putIfAbsent(node, new ArrayList<>());
        if (parent != null) {
            graph.get(node).add(parent);
            graph.get(parent).add(node);
        }

        buildGraph(node.left, node, graph);
        buildGraph(node.right, node, graph);
    }

    private static List<TreeNode> collectDown(TreeNode node, int k, List<TreeNode> result){
        if (node == null){
            return null;
        }
        if (k == 0)
            result.add(node);
        collectDown(node.left, k-1, result);
        collectDown(node.right, k-1, result);
        return result;
    }

    private static TreeNode findTarget(TreeNode root, int target){
        if (root == null){
            return null;
        }
        if (root.getData() == target){
            return root;
        }
        TreeNode left = findTarget(root.getLeft(), target);
        if (left != null){
            return left;
        }
        return findTarget(root.getRight(), target);
    }
}

class DistanceKSimplified {

    List<Integer> result = new ArrayList<>();

    public List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        dfs(root, target, k);
        return result;
    }

    /**
     * DFS to find target and collect nodes at distance k
     * @return distance from current node to target, or -1 if target not found
     */
    private int dfs(TreeNode node, TreeNode target, int k) {
        if (node == null) return -1;

        // 🎯 Found target node — collect all nodes downward at distance k
        if (node == target) {
            collectDown(node, k);
            return 0; // distance from target to itself = 0
        }

        // 🔹 Search target in left subtree
        int leftDist = dfs(node.left, target, k);
        if (leftDist != -1) {
            // If current node is k distance from target, add it
            if (leftDist + 1 == k)
                result.add(node.data);
            else
                // Otherwise, collect nodes in the opposite (right) subtree
                collectDown(node.right, k - leftDist - 2);
            return leftDist + 1;
        }

        // 🔹 Search target in right subtree
        int rightDist = dfs(node.right, target, k);
        if (rightDist != -1) {
            // If current node is k distance from target, add it
            if (rightDist + 1 == k)
                result.add(node.data);
            else
                // Otherwise, collect nodes in the opposite (left) subtree
                collectDown(node.left, k - rightDist - 2);
            return rightDist + 1;
        }

        // ❌ Target not found in either subtree
        return -1;
    }

    /**
     * Collect all nodes that are 'k' distance below the given node
     */
    private void collectDown(TreeNode node, int k) {
        if (node == null) return;
        if (k == 0) {
            result.add(node.data);
            return;
        }
        collectDown(node.left, k - 1);
        collectDown(node.right, k - 1);
    }

    // 🧪 Example test
    public static void main(String[] args) {
        /*
                 3
                / \
               5   1
              / \ / \
             6  2 0  8
               / \
              7   4
        */
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        TreeNode target = root.left; // Node 5
        int k = 2;

        DistanceKSimplified sol = new DistanceKSimplified();
        System.out.println(sol.distanceK(root, target, k)); // Output: [7, 4, 1]
    }
}