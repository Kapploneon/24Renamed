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

public class redBlackTree {

    private static binaryTreeNode sentinel = new binaryTreeNode();
    private static binaryTreeNode root;

    public redBlackTree(){
        sentinel.c = 'b';
        sentinel.keyValue = Integer.MIN_VALUE;
        root = sentinel;
    }

    private void firstNode(binaryTreeNode x){
        x.parent = sentinel;
        x.left = sentinel;
        x.right = sentinel;
        x.c = 'b';
        root = x;
    }

    public static void main(String arg[]){

        redBlackTree redBlackTree = new redBlackTree();
        binarySearchTree binarySearchTree = new binarySearchTree();

        Scanner input = new Scanner(System.in);

        // read values from file.
        inputReader inputReader = new inputReader("TreeInput.txt");
        System.out.print("K> Binary tree is preloaded from TreeInput.txt ");

        binaryTreeNode x = new binaryTreeNode();
        x.keyValue = inputReader.numbersArray[0];
        redBlackTree.firstNode(x);
        int[] numbersArray = inputReader.numbersArray;

        // Convert the array into red-black tree.
        for (int i = 1; i < numbersArray.length; i++) {
            redBlackTree.rbInsert(numbersArray[i]);
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
                    redBlackTree.rbInsert(input.nextInt());
                    continue;

                case 2:
                    System.out.print("Enter the element to delete: ");
                    redBlackTree.rbDelete(input.nextInt());

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

    private binarySearchTree binarySearchTree = new binarySearchTree();

    private void leftRotate(binaryTreeNode kPos){

//        binaryTreeNode kPos = binarySearchTree.position(root, k);

        if(kPos != null){
            binaryTreeNode y;
            y = kPos.right;
            kPos.right = y.left;

            if(y.left != sentinel){
                y.left.parent = kPos;
            }
            y.parent = kPos.parent;

            if(kPos.parent == sentinel){
                root = y;
            }else if(kPos == kPos.parent.left){
                kPos.parent.left = y;
            }else {
                kPos.parent.right = y;
            }
            y.left = kPos;
            kPos.parent = y;
        }

//        return root;
    }

    private void rightRotate(binaryTreeNode kPos){

//        binaryTreeNode kPos = binarySearchTree.position(root, k);

        if(kPos != null){
            binaryTreeNode y;
            y = kPos.left;
            kPos.left = y.right;

            if(y.right != sentinel){
                y.right.parent = kPos;
            }
            y.parent = kPos.parent;

            if(kPos.parent == sentinel){
                root = y;
            }else if(kPos == kPos.parent.right){
                kPos.parent.right = y;
            }else {
                kPos.parent.left = y;
            }
            y.right = kPos;
            kPos.parent = y;
        }

//        return root;
    }

    private void rbInsert(int z){

        binaryTreeNode y, zNode;
        y = sentinel;
        binaryTreeNode x = root;

        zNode = new binaryTreeNode();
        zNode.keyValue = z;

        while (x != sentinel){
            y = x;
            if(zNode.keyValue < x.keyValue)
                x = x.left;
            else
                x = x.right;
        }
        zNode.parent = y;
        if(y == sentinel){
            root = zNode;
        }else if(zNode.keyValue < y.keyValue){
            y.left = zNode;
        }else {
            y.right = zNode;
        }

        zNode.left = sentinel;
        zNode.right = sentinel;
        zNode.c = 'r';
        rbInsertFixUp(zNode);
    }

    private void rbInsertFixUp(binaryTreeNode zNode) {
        binaryTreeNode y;
        while (zNode.parent.c == 'r'){
            if(zNode.parent == zNode.parent.parent.left){
                y = zNode.parent.parent.right;
                if(y.c == 'r'){
                    zNode.parent.c = 'b';
                    y.c = 'b';
                    zNode.parent.parent.c = 'r';
                    zNode = zNode.parent.parent;
                }else {
                    if (zNode == zNode.parent.right) {
                        zNode = zNode.parent;
                        leftRotate(zNode);
                    }
                    zNode.parent.c = 'b';
                    zNode.parent.parent.c = 'r';
                    rightRotate(zNode.parent.parent);
                }
            }else {
                y = zNode.parent.parent.left;
                if(y.c == 'r'){
                    zNode.parent.c = 'b';
                    y.c = 'b';
                    zNode.parent.parent.c = 'r';
                    zNode = zNode.parent.parent;
                }else {
                    if(zNode == zNode.parent.left){
                        zNode = zNode.parent;
                        rightRotate(zNode);
                    }
                    zNode.parent.c = 'b';
                    zNode.parent.parent.c = 'r';
                    leftRotate(zNode.parent.parent);
                }
            }
        }
        root.c = 'b';
    }

    private void rbTransplant(binaryTreeNode u, binaryTreeNode v){
        if(u.parent == sentinel){
            root = v;
        }else if(u == u.parent.left){
            u.parent.left = v;
        }else {
            u.parent.right = v;
        }
        v.parent = u.parent;
    }

    private void rbDelete(int zInt){
        binaryTreeNode x, y, z;
        z = binarySearchTree.position(root, zInt);
        y = z;
        char yOrig;
        yOrig = y.c;

        if(z.left == sentinel){
            x = z.right;
            rbTransplant(z,z.right);
        }
        else if(z.right == sentinel){
            x = z.left;
            rbTransplant(z, z.left);
        }else {
            y = binarySearchTree.treeMinNode(z.right);
            yOrig = y.c;
            x = y.right;
            if(y.parent == z){
                x.parent = y;
            }else {
                rbTransplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            rbTransplant(z,y);
            y.left = z.left;
            y.left.parent = y;
            y.c = z.c;
        }
        if(yOrig == 'b'){
            rbDeleteFixUp(x);
        }
    }

    private void rbDeleteFixUp(binaryTreeNode x) {
        binaryTreeNode w;
        while (x != root && x.c == 'b'){
            if(x == x.parent.left){
                w = x.parent.right;
                if(w.c == 'r'){
                    w.c = 'b';
                    x.parent.c = 'r';
                    leftRotate(x.parent);
                    w = x.parent.right;
                }
                if(w.left.c == 'b' && w.right.c == 'b'){
                    w.c = 'r';
                    x = x.parent;
                }else{
                    if(w.right.c == 'b'){
                        w.left.c = 'b';
                        w.c = 'r';
                        rightRotate(w);
                        w = x.parent.right;
                    }
                    w.c = x.parent.c;
                    x.parent.c = 'b';
                    w.right.c = 'b';
                    leftRotate(x.parent);
                    x = root;
                }
            }else{
                w = x.parent.left;
                if(w.c == 'r'){
                    w.c = 'b';
                    x.parent.c = 'r';
                    rightRotate(x.parent);
                    w = x.parent.left;
                }
                if(w.right.c == 'b' && w.left.c == 'b'){
                    w.c = 'r';
                    x = x.parent;
                }else {
                    if(w.left.c == 'b'){
                        w.right.c = 'b';
                        w.c = 'r';
                        leftRotate(w);
                        w = x.parent.left;
                    }
                    w.c = x.parent.c;
                    x.parent.c = 'b';
                    w.left.c = 'b';
                    rightRotate(x.parent);
                    x = root;
                }
            }
        }
        x.c = 'b';
    }

}
