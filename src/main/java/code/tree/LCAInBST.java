package code.tree;

public class LCAInBST {
    public static Node findLCA(Node root, int p, int q) {
        while (root != null) {
            // If both nodes are smaller, go left
            if (p < root.data && q < root.data) {
                root = root.left;
            }
            // If both nodes are larger, go right
            else if (p > root.data && q > root.data) {
                root = root.right;
            }
            // Split point found → root is LCA
            else {
                return root;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Node root = new Node(20);
        root.left = new Node(10);
        root.right = new Node(30);
        root.left.left = new Node(5);
        root.left.right = new Node(15);
        root.right.left = new Node(25);
        root.right.right = new Node(35);

        System.out.println("LCA(5,15): " + findLCA(root, 5, 15).data); // 10
        System.out.println("LCA(25,35): " + findLCA(root, 25, 35).data); // 30
    }
}