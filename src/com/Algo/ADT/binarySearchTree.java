/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.ADT;

import com.Algo.inputReader;

import java.util.Scanner;

public class binarySearchTree {

    private static binarySearchTree binarySearchTree = new binarySearchTree();
    private String treeLine = "";
    private int levels;
    public int result;

    public static void main(String[] arg){
        Scanner input = new Scanner(System.in);
        inputReader inputReader = new inputReader("TreeInput.txt");
        System.out.print("K> Binary tree is preloaded from TreeInput.txt ");

        binaryTreeNode root = new binaryTreeNode();
        root.keyValue = inputReader.numbersArray[0];
        int[] numbersArray = inputReader.numbersArray;
        for (int i = 1; i < numbersArray.length; i++) {
            root = binarySearchTree.insert(root, numbersArray[i]);
        }

        System.out.println("Currently loaded InOrder Tree Traversal is: ");
        binarySearchTree.InOrderTraversal(root);

        String options = "\nK> Press the respective number for relevant operation. \n" +
                "1.Insert new element\n" +
                "2.Delete\n" +
                "3.Max\n" +
                "4.Min\n" +
                "5.Predecessor\n" +
                "6.Successor\n" +
                "7.InOrder Tree Traversal\n" +
                "8.Search\n" +
                "9.Exit\n";

        System.out.print(options);
        int userResponseInt;

        do{

            System.out.print("\nK> What you want to do next? ");
            userResponseInt = input.nextInt();

            switch (userResponseInt){
                case 1:
                    System.out.print("Enter the new element to insert ");
                    root = binarySearchTree.insert(root, input.nextInt());
                    continue;

                case 2:
                    System.out.print("Enter the element to delete: ");
                    root = binarySearchTree.treeDelete(root, input.nextInt());

                    if(binarySearchTree.result == Integer.MIN_VALUE)
                        System.out.print("Element not found. The element cannot be deleted.");
                    else
                        System.out.print("Element successfully deleted. New Inorder traversal is: ");
                    System.out.println("Current Tree nodes: ");
                    binarySearchTree.InOrderTraversal(root);
                    continue;

                case 3:
                    System.out.print("Tree Max is "+binarySearchTree.treeMax(root));
                    continue;

                case 4:
                    System.out.print("Tree Min is "+binarySearchTree.treeMin(root));
                    continue;

                case 5:
                    System.out.print("Enter the element whose predecessor you want to find: ");
                    int predecessor = binarySearchTree.treePredecessor(root, input.nextInt());

                    if(predecessor < Integer.MAX_VALUE)
                        System.out.print("The predecessor of the entered element is "+predecessor);
                    else
                        System.out.print("The is no predecessor of the entered element: ");
                    continue;

                case 6:
                    System.out.print("Enter the element whose successor you want to find: ");
                    int successor = binarySearchTree.treeSuccessor(root, input.nextInt());

                    if(successor > Integer.MIN_VALUE)
                        System.out.print("The successor of the entered element is "+successor);
                    else
                        System.out.print("The is no successor of the entered element: ");
                    continue;

                case 7:
                    binarySearchTree.InOrderTraversal(root);
                    continue;

                case 8:
                    System.out.print("Enter the element to search: ");
                    int key = input.nextInt();
//                    boolean present = binarySearchTree.treeSearch(root, key);
                    boolean present = binarySearchTree.iterativeTreeSearch(root, key);
                    System.out.print("The searched element is "+present);
                    continue;

                case 9:
                    break;
                default:
                    break;

            }

        }while(userResponseInt != 9);
    }

    private binaryTreeNode insert(binaryTreeNode root, int z){
        binaryTreeNode zNode = new binaryTreeNode();
        zNode.keyValue = z;

        binaryTreeNode y = null;
        binaryTreeNode x = root;
        while(x != null){
            y = x;
            if(zNode.keyValue < x.keyValue){
                x = x.left;
            }else{
                x = x.right;
            }
        }

        zNode.parent = y;
        if(y == null){
            root = zNode;
        }else if (zNode.keyValue < y.keyValue){
            y.left = zNode;
        }else {
            y.right = zNode;
        }

        return root;

    }

    public void InOrderTraversal(binaryTreeNode binaryTreeNode){
        if(binaryTreeNode != null){
            InOrderTraversal(binaryTreeNode.left);
            if(binaryTreeNode.keyValue > Integer.MIN_VALUE)
                System.out.print(binaryTreeNode.keyValue + ", ");
            InOrderTraversal(binaryTreeNode.right);
        }
    }

    private boolean treeSearch(binaryTreeNode binaryTreeNode, int key){
        if((key == binaryTreeNode.keyValue)){
            return true;
        }

        if( key < binaryTreeNode.keyValue && binaryTreeNode.left != null){
            return treeSearch(binaryTreeNode.left, key);
        }
        else if (binaryTreeNode.right != null){
            return treeSearch(binaryTreeNode.right, key);
        }

        return false;
    }

    public boolean iterativeTreeSearch(binaryTreeNode binaryTreeNode, int key){
        while(key != binaryTreeNode.keyValue){
            if (key < binaryTreeNode.keyValue && binaryTreeNode.left != null){
                binaryTreeNode = binaryTreeNode.left;
            }
            else if (binaryTreeNode.right != null){
                binaryTreeNode = binaryTreeNode.right;
            }
            else
                break;
        }

        if (binaryTreeNode.keyValue == key){
            return true;
        }
        return false;
    }

    public int treeMin(binaryTreeNode x){
        while (x.left != null){
            x = x.left;
        }
        return x.keyValue;
    }

    public binaryTreeNode treeMinNode(binaryTreeNode x){
        while (x.left != null){
            x = x.left;
        }
        return x;
    }

    public int treeMax(binaryTreeNode x){
        while (x.right != null){
            x = x.right;
        }
        return x.keyValue;
    }

    public binaryTreeNode position(binaryTreeNode x, int key){
        /*
        * Return:
        *       Null if the key not found.
        *       Pointer to the key value node.
        * */

        if(x == null)
            return null;

        // Find the position of key.
        while(key != x.keyValue){
            if (key < x.keyValue && x.left != null){
                x = x.left;
            }
            else if (x.right != null){
                x = x.right;
            }
            else
                break;
        }

        if (x.keyValue == key){
            return x;
        }

        return null;
    }

    public int treeSuccessor(binaryTreeNode root, int key){

        // Find the position of key.
        binaryTreeNode x;
        x = position(root, key);
        if (x.right == null && x.left == null && x.parent == null){
            return Integer.MIN_VALUE;
        }

        // Find the successor of key.
        if(x.right != null)
            return treeMin(x.right);

        binaryTreeNode y = x.parent;
        while(y != null && x == y.right){
            x = y;
            y = y.parent;
        }

        if(y != null)
            return y.keyValue;
        else
            return Integer.MIN_VALUE;
    }

    public int treePredecessor(binaryTreeNode root, int key){

        // Find the position of key.
        binaryTreeNode x;
        x = position(root, key);
        if (x.right == null && x.left == null && x.parent == null){
            return Integer.MAX_VALUE;
        }

        // Find the successor of key.
        if(x.left != null)
            return treeMax(x.left);

        binaryTreeNode y = x.parent;
        while(y != null && x == y.left){
            x = y;
            y = y.parent;
        }

        if(y != null)
            return y.keyValue;
        else
            return Integer.MAX_VALUE;
    }

    private binaryTreeNode transplant(binaryTreeNode root, binaryTreeNode u, binaryTreeNode v){
        if(u.parent == null){
            root = v;
        }else if (u == u.parent.left){
            u.parent.left = v;
        }else {
            u.parent.right = v;
        }

        if(v != null){
            v.parent = u.parent;
        }

        return root;
    }

    private binaryTreeNode treeDelete(binaryTreeNode root, int key){

        binaryTreeNode y, z;

        // Find the position of key.
        z = position(root, key);
        if (z == null){
            result = Integer.MIN_VALUE;
            return root;       // key value not found.
        }

        if(z.left == null && z.right != null){
            root = transplant(root, z, z.right);
        }else if(z.right == null && z.left != null){
            root = transplant(root, z, z.left);
        }else if(z.right != null && z.left != null){
            y = treeMinNode(z.right);
            if(y.parent != z){
                root = transplant(root, y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            root = transplant(root, z, y);
            y.left = z.left;
            y.left.parent = y;
        }else {

            if( root == z ) {
                root = null;
            }else {
                if (z.keyValue < z.parent.keyValue)
                    z.parent.left = null;
                else
                    z.parent.right = null;
                z.parent = null;    // unlink the only leaf node from the tree. To perform delete.
            }

        }

        result = Integer.MAX_VALUE;
        return root;
    }

}

class binaryTreeNode{

    binaryTreeNode parent;
    binaryTreeNode left;
    binaryTreeNode right;
    int keyValue;
    char c;

    binaryTreeNode(){
        parent = null;
        left = null;
        right = null;
        c = 'r';
    }

}
