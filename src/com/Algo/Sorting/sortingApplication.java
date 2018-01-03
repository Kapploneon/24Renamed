/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

import java.util.Scanner;

public class sortingApplication {


    public static void main(String arg[]){

        String dummy = "";
        String options = "Enter \n" +
                "1.InsertionSort\n" +
                "2.MergeSort\n" +
                "3.HeapSort\n" +
                "4.QuickSort\n" +
                "5.Randomized QuickSort\n" +
                "6.CountingSort\n" +
                "7.RadixSort\n" +
                "8.BucketSort\n" +
                "9.BubbleSort\n" +
                "10.ShellSort\n" +
                "11.SelectionSort";

        System.out.println(options);
        Scanner input = new Scanner(System.in);
        switch (input.nextInt()){
            case 1:
                insertionSort InsertionSort;
                InsertionSort = new insertionSort(dummy);
            case 2:
                mergeSort MergeSort;
                MergeSort = new mergeSort();
                MergeSort.mergeSort();
            case 3:
                heapSort HeapSort;
                HeapSort = new heapSort();
                HeapSort.heapSort();
            case 4:
                quickSort QuickSort;
                QuickSort = new quickSort();
                QuickSort.quickSort();
            case 5:
                quickSort RandomizedQuickSort;
                RandomizedQuickSort = new quickSort();
                RandomizedQuickSort.randomizedQuickSort();
            case 6:
                countingSort CountingSort;
                CountingSort = new countingSort();
                CountingSort.countingSort();
            case 7:
                radixSort radixSort;
                radixSort = new radixSort();
                radixSort.radixSort();
            case 8:
                bucketSort bucketSort = new bucketSort();
                bucketSort.bucketSort();
            case 9:
            case 10:

            default:


        }


    }
}
