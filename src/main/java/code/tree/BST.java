package code.tree;

public class BST{
    Node root;

    BST() {
        root = null;
    }

    // Recursive Search
    Node search(Node root, int key) {
        if (root == null || root.data == key)
            return root;

        if (key < root.data)
            return search(root.left, key);

        return search(root.right, key);
    }

    Node insert(Node root, int key) {
        if (root == null) {
            return new Node(key);
        }

        if (key < root.data) {
            root.left = insert(root.left, key);
        } else if (key > root.data) {
            root.right = insert(root.right, key);
        }

        return root; // unchanged root pointer
    }

    // Deletion is trickiest.
    // We have 3 cases when deleting a node:
    //	1.	No child (leaf) → just delete node.
    //	2.	One child → replace node with child.
    //	3.	Two children → find inorder successor (smallest node in right subtree), replace value, delete successor.
    Node delete(Node root, int key) {
        if (root == null) return null;

        if (key < root.data) {
            root.left = delete(root.left, key);
        } else if (key > root.data) {
            root.right = delete(root.right, key);
        } else {
            // Node found
            if (root.left == null)
                return root.right;
            else if (root.right == null)
                return root.left;

            // Case 3: Two children
            root.data = minValue(root.right);  // find inorder successor
            root.right = delete(root.right, root.data); // delete successor
        }

        return root;
    }

    int minValue(Node node) {
        int min = node.data;
        while (node.left != null) {
            min = node.left.data;
            node = node.left;
        }
        return min;
    }

    boolean checkBST(Node root){
        return checkBSTUtil(root, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    boolean checkBSTUtil(Node root, int min, int max){
        if (root == null) return true;
        if (root.data < min || root.data > max) return false;
        return checkBSTUtil(root.left, min, root.data) && checkBSTUtil(root.right, root.data, max);
    }
}
