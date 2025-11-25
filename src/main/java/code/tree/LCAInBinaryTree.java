package code.tree;

public class LCAInBinaryTree {
    public static Node findLCA(Node root, Node p, Node q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        Node left = findLCA(root.left, p, q);
        Node right = findLCA(root.right, p, q);

        // If both sides are non-null → this is the LCA
        // means p and q are on different sides
        if (left != null && right != null) return root;

        // Otherwise return whichever is not null
        // means p and q are on the same side and one of them is the LCA
        return left != null ? left : right;
    }

    public static void main(String[] args) {
        Node root = new Node(3);
        root.left = new Node(5);
        root.right = new Node(1);
        root.left.left = new Node(6);
        root.left.right = new Node(2);
        root.right.left = new Node(0);
        root.right.right = new Node(8);
        root.left.right.left = new Node(7);
        root.left.right.right = new Node(4);

        Node lca = findLCA(root, root.left, root.right); // LCA(5,1)
        System.out.println("LCA(5,1): " + lca.data); // 3

        Node lca2 = findLCA(root, root.left.right.left, root.left.right.right); // LCA(7,4)
        System.out.println("LCA(7,4): " + lca2.data); // 2
    }
}