/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

import java.util.Arrays;
import java.util.Random;

public class quickSort {


    public void quickSort(){

        // read input.
        insertionSort insertionSortObject = new insertionSort();
        quick_Sort(insertionSortObject.numbersArray, 0, insertionSortObject.numbersArray.length - 1);
        insertionSortObject.writeOutput("quickSort");

    }

    public void randomizedQuickSort(){

        // read input.
        insertionSort insertionSortObject = new insertionSort();
        randomizedQuick_Sort(insertionSortObject.numbersArray, 0, insertionSortObject.numbersArray.length - 1);
        insertionSortObject.writeOutput("Randomized_quickSort");

    }

    private void quick_Sort(int[] localNumberArray, int p, int r){
        int q;
        if( p < r ){
            q = partition(localNumberArray, p, r);
            quick_Sort(localNumberArray, p, q-1);
            quick_Sort(localNumberArray, q+1, r);
        }
    }

    private int partition(int[] localNumberArray, int p, int r){
        int pivot = localNumberArray[r];
        int i = p-1;
        int dummy;

        for(int j = p; j < r; j++){
            if(localNumberArray[j] <= pivot){
                i++;

                // Exchange localNumberArray[i] with localNumberArray[j].
                dummy = localNumberArray[j];
                localNumberArray[j] = localNumberArray[i];
                localNumberArray[i] = dummy;
            }
        }

        // Exchange localNumberArray[i+1] with localNumberArray[pivot].
        dummy = localNumberArray[r];
        localNumberArray[r] = localNumberArray[i+1];
        localNumberArray[i+1] = dummy;
//        System.out.println(Arrays.toString(localNumberArray));            // for debug purpose.
        return i+1;
    }

    public int randomized_partition(int[] localNumberArray, int p, int r){
        int randomPivot = p + (int)(Math.random()*( ( r - p ) + 1) );
        int dummy;

        // Swap r`th element with randomPivot`th element.
        dummy = localNumberArray[r];
        localNumberArray[r] = localNumberArray[randomPivot];
        localNumberArray[randomPivot] = dummy;

        return partition(localNumberArray,p,r);
    }

    private void randomizedQuick_Sort(int[] localNumberArray, int p, int r){
        int q;
        if( p < r ){
            q = randomized_partition(localNumberArray, p, r);
            randomizedQuick_Sort(localNumberArray, p, q-1);
            randomizedQuick_Sort(localNumberArray, q+1, r);
        }
    }

}
