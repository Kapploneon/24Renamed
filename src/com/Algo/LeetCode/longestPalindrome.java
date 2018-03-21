/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.LeetCode;

import java.util.Scanner;

class longestPalindrome {


    private String longestPalin(String s) {
        int i, j = 0, jmin = 0, imax = 0;
        int jx = 0, jxmin = 0, ixmax = 0;
        int local = 0;
        String[] ary = s.split("");

        if(ary.length == 0)
            return "";

        String palin = "";

        for(i = 1; i < ary.length; i++) {

            // 1 previous.
            if((j-1) >= 0) {
                if (ary[j - 1].equals(ary[i])) {

                    if ((j - 1) == 0) {
                        jmin = 0;
                        imax = i;
                        j = i;
                        local = 0;
                    }else {
                        local = 2;
                        j--;
                    }

                }else if(local == 2){
                    jmin = j;
                    imax = i-1;
                    j = i;
                    local = 0;
                }else{
                    j = i;
                }

            }else {
                j = i;
            }

            // Just previous.
            if((jx) >= 0){
                if (ary[jx].equals(ary[i])) {

                    if ((jx) == 0) {

                        jxmin = 0;
                        ixmax = i;
                        jx = i;
                        local = 0;

                    }else {
                        local = 1;
                        jx--;
                    }
                }else if(local == 1){
                    jxmin = j+1;
                    ixmax = i-1;
                    jx = i;
                    local = 0;
                }else {
                    jx = i;
                }

            }else {
                jx = i;
            }

        }

        if(local == 1 ){
            jxmin = jx + 1;
            ixmax = i - 1;
        }else if(local == 2){
            jmin = j;
            imax = i - 1;
        }

        if((ixmax - jxmin) > (imax - jmin)) {
            imax = ixmax;
            jmin = jxmin;
        }

        for(i = jmin; i <= imax; i++){
            palin = palin + ary[i];
        }

        return palin;
    }

    public static void main(String[] arg){
        longestPalindrome Ob = new longestPalindrome();

        String x;
        Scanner input = new Scanner(System.in);

        while (input.hasNext()) {
            x = Ob.longestPalin(input.nextLine());
//            x = Ob.reverseInteger(2147483647);
            System.out.println(x);
        }
    }
}

