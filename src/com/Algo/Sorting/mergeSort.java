/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.Sorting;

import static java.lang.Math.floor;

public class mergeSort {

    public void mergeSort(){
        insertionSort sortingObject = new insertionSort();
        merge_Sort(sortingObject.numbersArray,0, sortingObject.numbersArray.length-1);
        sortingObject.writeOutput("mergeSort");
    }

    private void merge_Sort(int[] localNumberArray,int p, int r){
        if(p < r){
            int q = (int) floor((p+r)/2);
            merge_Sort(localNumberArray,p,q);
            merge_Sort(localNumberArray,q+1,r);
            merge(localNumberArray,p,q,r);
        }
    }

    private void merge(int[] localNumberArray,int p, int q, int r){
        int n1 = q-p+1;
        int n2 = r-q;
        int[] left = new int[n1+1];
        int[] right = new int[n2+1];

        for(int i = 0;i<n1;i++){
            left[i] = localNumberArray[p+i];
        }
        for(int j = 0;j<n2;j++){
            right[j] = localNumberArray[q+j+1];
        }
        left[n1] = Integer.MAX_VALUE;
        right[n2] = Integer.MAX_VALUE;

        int i = 0;
        int j = 0;

        for(int k = p;k<r;k++){
            if(left[i] <= right[j]){
                localNumberArray[k] = left[i];
                i++;
            }
            else{
                localNumberArray[k] = right[j];
                j++;
            }
        }
    }

}
