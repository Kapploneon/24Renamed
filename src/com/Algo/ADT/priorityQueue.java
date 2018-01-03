/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.ADT;

import com.Algo.Sorting.heapSort;

import java.util.Arrays;
import java.util.Scanner;

public class priorityQueue {
    heapSort heapSort = new heapSort();
    private int[] priorityQ;
    private int heapSize, capacity;

    public void priorityQueue(int capacity) {
       this.capacity = capacity;
       priorityQ = new int[capacity];
       heapSize = -1;
       Arrays.fill(priorityQ,Integer.MIN_VALUE);
    }

    private void heapIncreaseKey(int i, int key){
        int dummy;

        if( key < priorityQ[i] ){
            System.err.println("New Key is smaller than current key.");
        }

        priorityQ[i] = key;

        while ( i > 0 && priorityQ[heapSort.parent(i)] < priorityQ[i]){

            // Exchange i with parent of i.
            dummy = priorityQ[i];
            priorityQ[i] = priorityQ[heapSort.parent(i)];
            priorityQ[heapSort.parent(i)] = dummy;

            i = heapSort.parent(i);
        }

    }

    private void maxHeapInsert(int key){
        if (heapSize == priorityQ.length-1){
            System.err.print("\nHeap overflow. No more space to add new element.");
            return;
        }
        heapSize++;
        priorityQ[heapSize] = Integer.MIN_VALUE;
        heapIncreaseKey(heapSize, key);
    }

    private int heapMax(){
        return priorityQ[0];
    }

    private int heapMaxExtract(){
        if (heapSize < 0){
            System.err.println("\nHeap underflow. No element to remove. Please try inserting first.");
            return 0;
        }
        int max = priorityQ[0];
        priorityQ[0] = priorityQ[heapSize];
        heapSize--;
        heapSort.maxHeapify(priorityQ,0, heapSize);
        return max;
    }

    private int getHeapSize(){
        return heapSize;
    }

    private void clear(){
        priorityQ = new int[this.capacity];
    }

    private String checkEmpty(){
        if(heapSize == -1){
            return "Priority Queue is Empty";
        }else {
            return "Priority Queue is Not Empty";
        }
    }

    private String checkFull(){
        if(heapSize == priorityQ.length){
            return "Priority Queue is Full";
        }else {
            return "Priority Queue is Not Full";
        }
    }

    public static void main(String[] arg){
        Scanner input;
        priorityQueue priorityQueueObject = new priorityQueue();
        String options = "Priority Queue Operations\n" +
                        "1. insert\n" +
                        "2. remove\n" +
                        "3. peep\n" +
                        "4. check empty\n" +
                        "5. check full\n" +
                        "6. clear\n" +
                        "7. size";

        input = new Scanner(System.in);

        System.out.print("K> "+"Enter the capacity of the Queue:");
        priorityQueueObject.priorityQueue(input.nextInt());
        System.out.println("K> "+options);

        char ch;
        /* Performs priority Queue operations */
        do{

            System.out.print("K> ");
            int choice = input.nextInt();
            switch (choice){
                case 1:
                    System.out.print("Enter the value of the new element to insert:");
                    priorityQueueObject.maxHeapInsert(input.nextInt());
                    break;
                case 2:
                    System.out.println("Element removed:"+priorityQueueObject.heapMaxExtract());
                    break;
                case 3:
                    System.out.println("Current top priority element is:"+priorityQueueObject.heapMax());
                    break;
                case 4:
                    System.out.println(priorityQueueObject.checkEmpty());
                    break;
                case 5:
                    System.out.println(priorityQueueObject.checkFull());
                    break;
                case 6:
                    priorityQueueObject.clear();
                    System.out.println("Priority Queue cleared");
                    break;
                case 7:
                    System.out.println("Heap size is:"+priorityQueueObject.getHeapSize());
                    break;
                default:
                    System.out.println("Wrong entry. Please try again");
                    break;
            }

            System.out.print("K> "+"Do you want to continue (Type y or n):");
            ch = input.next().charAt(0);
        } while (ch == 'Y'|| ch == 'y');

    }

}
