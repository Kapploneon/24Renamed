/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

public class radixSort {

    public void radixSort(){

        insertionSort insertionSortObject = new insertionSort();
        radix_Sort(insertionSortObject.numbersArray);
        insertionSortObject.writeOutput("radixSort");

    }

    private void radix_Sort(int[] localNumberArray){

        countingSort countingSortObject = new countingSort();

        int max = countingSortObject.findMax(localNumberArray);

        for(int exp = 1; max/exp > 0; exp *= 10)
            countingSortObject.counting_Sort_On_Index(localNumberArray, exp);
    }
}
