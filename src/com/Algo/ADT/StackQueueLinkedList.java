/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.ADT;

import com.Algo.inputReader;

import java.util.List;
import java.util.Scanner;

public class StackQueueLinkedList {

    public static void main(String arg[]){

        Scanner input = new Scanner(System.in);

        inputReader inputReader = new inputReader("TreeInput.txt");

        System.out.print("K> What do you want to implement?");
        String options = "Press\n" +
                "1. Stack\n" +
                "2. Queue\n" +
                "3. Linked List\nK> ";

        System.out.print(options);
        int response = input.nextInt();
        StackQueueLinkedList thisObject = new StackQueueLinkedList();
        node head = new node();
        node tail;
        tail = head;

        switch(response){
            case 1:
                // Sentinel.
                head.keyValue = Integer.MIN_VALUE;
                head.next = null;
                head.prev = null;

                for(int i = 0; i < inputReader.numbersArray.length; i++){
                    head = thisObject.push(head, inputReader.numbersArray[i]);
                }

                String stackOptions = "K> Press\n" +
                        "1. Push\n" +
                        "2. Pop\n" +
                        "3. Exit";
                System.out.println(stackOptions);

                int stackResponse;

                do{
                    System.out.println("K> What do you want to do next? ");
                    System.out.print("K> ");
                    stackResponse = input.nextInt();

                    switch (stackResponse){
                        case 1:
                            System.out.print("Enter the element to push: ");
                            int x = input.nextInt();
                            head = thisObject.push(head, x);
                            continue;
                        case 2:
                            head = thisObject.pop(head);
                            continue;
                        default:
                            break;
                    }
                }while ( stackResponse < 3 );

            case 2:

                // Preload Queue from TreeInput.txt.
                for(int i = 0; i < inputReader.numbersArray.length; i++){
                    tail = thisObject.enqueue(tail, inputReader.numbersArray[i]);
                }

                String QueueOptions = "K> Press\n" +
                        "1. Enqueue\n" +
                        "2. Dequeue\n" +
                        "3. Exit";
                System.out.println(QueueOptions);

                int queueResponse;

                do{
                    System.out.println("K> What do you want to do next? ");
                    System.out.print("K> ");
                    queueResponse = input.nextInt();

                    switch (queueResponse){
                        case 1:
                            System.out.print("Enter the element to Enqueue: ");
                            int x = input.nextInt();
                            tail = thisObject.enqueue(tail, x);
                            continue;
                        case 2:
                            head = thisObject.dequeue(head);
                            continue;
                        default:
                            break;
                    }
                }while ( queueResponse < 3);

            case 3:
                head.next = head;
                head.prev = head;
                head.keyValue = Integer.MIN_VALUE;

                // Preload LinkedList from TreeInput.txt.
                for(int i = 0; i < inputReader.numbersArray.length; i++){
                    head = thisObject.listInsert(head, inputReader.numbersArray[i], Integer.MIN_VALUE);
                }

                String listOptions = "K> Press\n" +
                        "1. Insert\n" +
                        "2. Delete\n" +
                        "3. Search\n" +
                        "4. Print the linked list\n" +
                        "5. Exit";
                System.out.println(listOptions);

                int listResponse;

                do{
                    System.out.println("K> What do you want to do next? ");
                    System.out.print("K> ");
                    listResponse = input.nextInt();

                    switch (listResponse){
                        case 1:
                            System.out.print("Enter the element to insert: ");
                            int x = input.nextInt();
                            System.out.print("Enter the element after which you want to insert: ");
                            int y = input.nextInt();
                            head = thisObject.listInsert(head, x, y);
                            continue;
                        case 2:
                            System.out.print("Enter the element to delete: ");
                            int x1 = input.nextInt();
                            thisObject.listDelete(head, x1);
                            continue;
                        case 3:
                            System.out.print("Enter the element to search: ");
                            int x2 = input.nextInt();
                            node dummy = thisObject.listSearch(head, x2);
                            if (dummy != null){
                                System.out.println("Search Successful");
                            }else
                                System.out.println("Search Unsuccessful");
                            continue;
                        case 4:
                            thisObject.listPrint(head);
                            continue;
                        default:
                            break;
                    }
                }while ( listResponse < 5);

            default:
                break;
        }

    }

    // Stack.
    private node push(node S, int x){
        node newNode = new node();
        S.next = newNode;
        newNode.prev = S;
        newNode.keyValue = x;
        return newNode;
    }

    private node pop(node S){
        if( S != null && S.prev != null) {
            S = S.prev;
            System.out.println("Popped element is "+S.next.keyValue);
            S.next = null;
            return S;
        }
        else
            System.err.println("Stack Underflow. No element to pop.");
        return S;
    }

    // Queue.
    private node enqueue(node q, int x){
        q.keyValue = x;
        q.next = new node();
        q = q.next;
        return q;
    }

    private node dequeue(node q){
        if(q == null || q.next == null) {
            System.err.println("Queue underflow. No more elements to dequeue.");
            return q;
        }
        System.out.println("DeQueued element is " + q.keyValue);
        q = q.next;
        return q;
    }

    // Doubly linked list.
    private node listSearch(node L, int k){
        node x = L.next;
        while(x != L && x.keyValue != k){
            x = x.next;
        }

        if(x.keyValue != k)
            return null;

        return x;
    }

    private node listInsert(node L, int k, int successor){
        node L1;
        L1 = L;
        if(successor > Integer.MIN_VALUE)
            L1 = listSearch( L, successor );
        if(L1 == null)
            L1 = L;
        node y = new node();

        y.keyValue = k;
        y.next = L1.next;
        y.prev = L1;
        L1.next = y;

        return L;

    }

    private void listDelete(node L, int x){
        if(x > Integer.MIN_VALUE)
            L = listSearch(L, x);
        if(L == null) {
            System.out.println("No such element to delete.");
            return;
        }
        System.out.println("Element "+L.keyValue+ " deleted");
        L.prev.next = L.next;
        L.next.prev = L.prev;
    }

    private void listPrint(node head){
        do{
            System.out.print(head.keyValue +" -> ");
            head = head.next;
        }while (head.keyValue != Integer.MIN_VALUE);
    }

    private static class node{

        int keyValue;
        node next;
        node prev;

    }

}

