/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;


class countingSort {

    public void countingSort(){

        insertionSort insertionSortObject = new insertionSort();
        counting_Sort(insertionSortObject.numbersArray);
        insertionSortObject.writeOutput("countingSort");

    }

    private void counting_Sort(int[] localNumberArray){
        int[] sortedNumberArray = new int[localNumberArray.length];
        int k = findMax(localNumberArray);
        int[] dummyCount = new int[k+1];


        for (int aLocalNumberArray : localNumberArray) {
            dummyCount[aLocalNumberArray]++;
        }
        // dummyCount now contains number of elements = j;


        for(int j = 1; j < dummyCount.length; j++){
            dummyCount[j] = dummyCount[j] + dummyCount[j-1];
        }
        // dummyCount now contains number of elements <= j;

        for(int j = localNumberArray.length - 1; j>=0; j--){
            // Here -1 is to take array element 0 into account.
            sortedNumberArray[dummyCount[localNumberArray[j]] - 1] = localNumberArray[j];
            dummyCount[localNumberArray[j]]--;
        }
        // Now the sortedNumberArray contains sorted output of localNumberArray.

//        localNumberArray = sortedNumberArray.clone();         <---- Does not work.
        // Copy elements of sortedNumberArray[] to localNumberArray[]
        System.arraycopy(sortedNumberArray, 0, localNumberArray, 0, sortedNumberArray.length);

//        System.out.println("sortedNumberArray "+Arrays.toString(sortedNumberArray));      // Used for debug.
//        System.out.println("localNumberArray "+Arrays.toString(localNumberArray));      // Used for debug.

    }

    public void counting_Sort_On_Index(int[] localNumberArray, int exp){
        int[] sortedNumberArray = new int[localNumberArray.length];
        int k = findMax(localNumberArray);
        int[] dummyCount = new int[10];


        for (int aLocalNumberArray : localNumberArray) {
            dummyCount[(aLocalNumberArray / exp) % 10]++;
        }
        // dummyCount now contains number of elements = j;


        for(int j = 1; j < dummyCount.length; j++){
            dummyCount[j] = dummyCount[j] + dummyCount[j-1];
        }
        // dummyCount now contains number of elements <= j;

        for(int j = localNumberArray.length - 1; j>=0; j--){
            // Here -1 is to take array element 0 into account.
            sortedNumberArray[dummyCount[ ( localNumberArray[j] / exp ) % 10 ] - 1] = localNumberArray[j];
            dummyCount[ ( localNumberArray[j] / exp ) % 10 ]--;
        }
        // Now the sortedNumberArray contains sorted output of localNumberArray.

        // Copy elements of sortedNumberArray[] to localNumberArray[]
        System.arraycopy(sortedNumberArray, 0, localNumberArray, 0, sortedNumberArray.length);

    }

    // To find the max int value in the array.
    public int findMax(int[] localNumberArray){
        int localMax = localNumberArray[0];

        for (int aLocalNumberArray : localNumberArray) {
            if (aLocalNumberArray > localMax) {
                localMax = aLocalNumberArray;
            }
        }

        return localMax;
    }

}
