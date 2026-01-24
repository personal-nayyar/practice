//package LLD2.fileSystem_Unravel.urravel;
//
//import design_pattern.structural.Facade;
//
//import java.util.*;
///*
//Output:Suppose you have an object of Directory by the name of /test
//Just print the following
///abc.txt
///test/abc.txt
///test/internal_test
//
//Example:
//Input-
///
//output-
///test
///abc.txt
//
//* */
///// Node
/////     FileNode
/////     DirNode
/////
/////
//public class Urravel {}
//
//class Node {
//    String name;
//    int size;
//    Date createdDate;
//    // other attributes like owner, permissions, memorySize, group etc.
//
//    Node(String name){
//        this.name = name;
//        this.createdDate = new Date();
//        size = 0;
//    }
//}
//
//class DirNode extends Node {
//    DirNode(String name){
//        super(name);
//    }
//}
//
//class FileNode extends Node {
//    FileNode(String name){
//        super(name);
//    }
//}
//
//class DirectorySystem{
//    public static void main(String[] args) {
//        DirectorySystem directorySystem = new DirectorySystem();
//        directorySystem.addChild("/", new DirNode("test"));
//        directorySystem.addChild("/", new FileNode("abc.text"));
//        directorySystem.addChild("/test", new FileNode("internal_test"));
//
//        directorySystem.ls("/");
//
//    }
//    Map<String, List<Node>> directoriesMap;// /
//    DirectorySystem(){
//        directoriesMap = new HashMap<>();
//        directoriesMap.put("/", new ArrayList<>());
//    }
//
//    Node addChild(String name, Node child){
//        List<Node> childs = directoriesMap.getOrDefault(name, new ArrayList<>());
//        childs.add(child);
//        return child;
//    }
//
//    public void ls(String name){
//        List<Node> childs =  directoriesMap.get(name);
//        childs.stream().filter(child -> child instanceof FileNode).map(child -> child.name).forEach(System.out::println);
//        childs.stream().filter(child -> child instanceof DirNode).map(child -> child.name).forEach(System.out::println);
//    }
//}
//
//class Main{
//    public static void main(String[] args) {
//    }
//}
