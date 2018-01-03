/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

import static java.lang.Math.floor;

public class heapSort {

    public void heapSort(){
        insertionSort sortingObject = new insertionSort();
        heap_Sort(sortingObject);
        sortingObject.writeOutput("heapSort");
    }

    private void heap_Sort(insertionSort sortingObject){
        int dummy;

        buildMaxHeap(sortingObject);
//        sortingObject.writeOutput("heapSort_after_buildMax");                 // For debug purpose.
        for(int i = sortingObject.numbersArray.length - 1; i >= 1; i = i - 1 ){
            dummy = sortingObject.numbersArray[i];
            sortingObject.numbersArray[i] = sortingObject.numbersArray[0];
            sortingObject.numbersArray[0] = dummy;
            sortingObject.heapSize = sortingObject.heapSize - 1;
            maxHeapify(sortingObject,0);
//            sortingObject.writeOutput("heapSort_after_round"+i);                 // For debug purpose.
        }
    }

    private void buildMaxHeap(insertionSort sortingObject){

        sortingObject.heapSize = sortingObject.numbersArray.length - 1;

        for(int i = (int) floor((sortingObject.numbersArray.length/2)-1); i >= 0; i = i - 1 ){
            maxHeapify(sortingObject,i);
        }

    }

    public void maxHeapify(int[] sortingObjectArray, int i, int sortingObject_heapSize){
        int l = left(i);
        int r = right(i);
        int largest;
        int dummy;

        if (l <= sortingObject_heapSize && sortingObjectArray[l] > sortingObjectArray[i]){
            largest = l;
        }else {
            largest = i;
        }

//        if (r < sortingObject.heapSize){
        if (r <= sortingObject_heapSize && sortingObjectArray[r] > sortingObjectArray[largest] ){
            largest = r;
        }
//        }

        if ( largest != i ){
            dummy = sortingObjectArray[i];
            sortingObjectArray[i] = sortingObjectArray[largest];
            sortingObjectArray[largest] = dummy;
            maxHeapify(sortingObjectArray, largest, sortingObject_heapSize);
        }
    }

    public void maxHeapify(insertionSort sortingObject, int i){
        maxHeapify(sortingObject.numbersArray, i, sortingObject.heapSize);
    }

    public int parent(int i){
        return (int) floor(i/2);
    }

    private int left(int i){
        return 2*i+1;
    }

    private int right(int i){
        return 2*i+2;
    }

}
