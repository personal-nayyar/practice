package code.tree;

// Basic Node structure
class Node {
    int data;
    Node left, right;
    Node(int data) {
        this.data = data;
        left = right = null;
    }
}

// Example: Traversals
class BinaryTree {
    Node root;

    // Inorder: Left → Root → Right
    void inorder(Node node) {
        if (node == null) return;
        inorder(node.left);
        System.out.print(node.data + " ");
        inorder(node.right);
    }

    // Preorder: Root → Left → Right
    void preorder(Node node) {
        if (node == null) return;
        System.out.print(node.data + " ");
        preorder(node.left);
        preorder(node.right);
    }

    // Postorder: Left → Right → Root
    void postorder(Node node) {
        if (node == null) return;
        postorder(node.left);
        postorder(node.right);
        System.out.print(node.data + " ");
    }
}