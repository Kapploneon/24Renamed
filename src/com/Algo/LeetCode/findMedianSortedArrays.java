/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.LeetCode;

import com.Algo.inputReader;

import java.util.Scanner;

class findMedianSortedArrays {

    private static inputReader inputReader = new inputReader();
    private static inputReader inputReader2 = new inputReader();

    private double findMedian(int start1, int end1, int start2, int end2) {

        int n1, n2;
        double med1, med2;
        n1 = (end1 - start1)/2;
        n2 = (end2 - start2)/2;

        med1 = inputReader.numbersArray[n1];
        med2 = inputReader2.numbersArray[n2];

        if(med1 > med2)
            return findMedian(start1, n1, n2, end2);
        else
            return findMedian(n1, end1, start2, n2);

    }

    public static void main(String[] arg){

        findMedianSortedArrays Ob = new findMedianSortedArrays();

        double x = 0;

        x = Ob.findMedian(0, inputReader.numbersArray.length - 1, 0, inputReader2.numbersArray.length - 1);
        System.out.println(x);

    }
}

