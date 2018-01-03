/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

import java.util.ArrayList;
import java.util.List;

public class bucketSort {
    private static final int DEFAULT_BUCKET_SIZE = 5;

    public void bucketSort(){
        insertionSort insertionSort = new insertionSort();
        bucket_Sort(insertionSort.numbersArray, DEFAULT_BUCKET_SIZE);
        insertionSort.writeOutput("bucketSort");
    }

    private void bucket_Sort(int[] localNumberArray, int bucketSize){

        // Determine minimum and maximum value.
        int max = localNumberArray[0];
        int min = localNumberArray[0];

        for(int i = 1; i < localNumberArray.length; i++){
            if (localNumberArray[i] < min){
                min = localNumberArray[i];
            }else if (localNumberArray[i] > max){
                max = localNumberArray[i];
            }
        }

        // Initialise buckets
        int bucketCount = (max - min) / bucketSize + 1;
        List<List<Integer>> buckets = new ArrayList<List<Integer>>(bucketCount);
        for (int aLocalNumberArray : localNumberArray) {
            buckets.add( new ArrayList<Integer>() );
        }

        for (int aLocalNumberArray : localNumberArray) {
            buckets.get((aLocalNumberArray - min) / bucketSize).add(aLocalNumberArray);
        }

        int currentIndex = 0;
        for(int i = 0; i < buckets.size(); i++){
            Integer[] bucketArray = new Integer[buckets.get(i).size()];
            bucketArray = buckets.get(i).toArray(bucketArray);

            insertionSort insertionSort = new insertionSort(bucketArray);
            insertionSort.Sort();

            for(int j = 0; j < insertionSort.numbersArray.length; j++){
                localNumberArray[currentIndex++] = insertionSort.numbersArray[j];
            }

        }
    }
}
