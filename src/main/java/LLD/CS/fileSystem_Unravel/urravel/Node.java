package LLD.CS.fileSystem_Unravel.urravel;

import java.util.*;

// Abstract Node
abstract class Node {
    String name;
    Date createdDate;
    long size;

    Node(String name) {
        this.name = name;
        this.createdDate = new Date(); // assign current timestamp
        this.size = 0; // default
    }
    abstract boolean isDirectory();
}

// File Node
class FileNode extends Node {
    String content;
    FileNode(String name, long size) {
        super(name);
        this.size = size;
        this.content = "";
    }
    @Override
    boolean isDirectory() {
        return false;
    }
}

// Directory Node
class DirectoryNode extends Node {
    Map<String, Node> children = new HashMap<>();
    DirectoryNode(String name) {
        super(name);
    }
    @Override
    boolean isDirectory() {
        return true;
    }
    void addChild(Node child) {
        children.put(child.name, child);
    }
    Node getChild(String name) {
        return children.get(name);
    }
}

// Main Directory System
class DirectorySystem {
    private DirectoryNode root; // root directory

    public DirectorySystem() {
        root = new DirectoryNode("root");
    }

    private DirectoryNode traverseAndGetDir(String path) {
        if (path.equals(".") || path.equals("/")) return root;
        String[] parts = path.split("/");
        DirectoryNode curr = root;
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;
            Node child = curr.getChild(part);
            if (child == null || !child.isDirectory()) {
                throw new IllegalArgumentException("Invalid path: " + path);
            }
            curr = (DirectoryNode) child;
        }
        return curr;
    }

    // mkdir
    public void mkdir(String path) {
        String[] parts = path.split("/");
        DirectoryNode curr = root;
        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;
            Node child = curr.getChild(part);
            if (child == null) {
                DirectoryNode newDir = new DirectoryNode(part);
                curr.addChild(newDir);
                curr = newDir;
            } else if (child.isDirectory()) {
                curr = (DirectoryNode) child;
            } else {
                throw new IllegalArgumentException("Path conflicts with a file: " + path);
            }
        }
    }

    // addFile
    public void addFile(String path, long size) {
        int idx = path.lastIndexOf("/");
        String dirPath = path.substring(0, idx);
        String fileName = path.substring(idx + 1);
        DirectoryNode dir = traverseAndGetDir(dirPath.isEmpty() ? "/" : dirPath);
        dir.addChild(new FileNode(fileName, size));
    }

    // ls
    public void ls(String path) {
        DirectoryNode dir = traverseAndGetDir(path);
        for (String name : dir.children.keySet()) {
            System.out.println(name);
        }
    }

    // getSortedList
    public void getSortedList(String path, String sortBy) {
        DirectoryNode dir = traverseAndGetDir(path);
        List<Node> list = new ArrayList<>(dir.children.values());

        Comparator<Node> comparator;
        switch (sortBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(n -> n.name.toLowerCase());
                break;
            case "date":
                comparator = Comparator.comparing(n -> n.createdDate);
                break;
            case "size":
                comparator = Comparator.comparingLong(n -> n.size);
                break;
            default:
                throw new IllegalArgumentException("Unsupported sort parameter: " + sortBy);
        }

        list.sort(comparator);
        for (Node node : list) {
            System.out.println(node.name + (node.isDirectory() ? "/" : "")
                               + " | size=" + node.size
                               + " | created=" + node.createdDate);
        }
    }

    // Example run
    public static void main(String[] args) throws InterruptedException {
        DirectorySystem ds = new DirectorySystem();

        ds.mkdir("/api/test");
        ds.addFile("/api/test/fileB.txt", 200);
        Thread.sleep(1000); // delay to show different timestamps
        ds.addFile("/api/test/fileA.txt", 100);
        Thread.sleep(1000);
        ds.mkdir("/api/test/docs");

        System.out.println("LS of /test:");
        ds.ls("/api/test");

        System.out.println("\nSorted by name:");
        ds.getSortedList("/test", "name");

        System.out.println("\nSorted by date:");
        ds.getSortedList("/test", "date");

        System.out.println("\nSorted by size:");
        ds.getSortedList("/test", "size");
    }
}