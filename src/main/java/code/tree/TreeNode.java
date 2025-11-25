package code.tree;

import lombok.Getter;

@Getter
public class TreeNode{
    int data;
    TreeNode left;
    TreeNode right;

    TreeNode(int data){
        this.data = data;
    }

    public String toString(){
        return String.valueOf(data);
    }
}