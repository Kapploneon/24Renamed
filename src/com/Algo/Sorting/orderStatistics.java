/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

public class orderStatistics {



    private int randomizedSelect(int[] numberArray, int p, int r, int i){
        quickSort quickSort = new quickSort();
        if(p == r)
            return numberArray[p];
        int q = quickSort.randomized_partition(numberArray, p, r);
        int k = q - p + 1;
        if(i == k)
            return numberArray[q];
        else if(i < k)
            return randomizedSelect(numberArray, p, q - 1, i);
        else
            return randomizedSelect(numberArray, q + 1, r, i - k);
    }

    private int min(int[] numberArray){
        int min;
        min = numberArray[0];

        for(int i = 1; i < numberArray.length; i++){
            if(min > numberArray[i]){
                min = numberArray[i];
            }
        }
        return min;
    }

    private int max(int[] numberArray){
        int max;
        max = numberArray[0];

        for(int i = 1; i < numberArray.length; i++){
            if(max < numberArray[i]){
                max = numberArray[i];
            }
        }
        return max;
    }
}
