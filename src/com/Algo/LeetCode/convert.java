/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.LeetCode;

import java.util.Arrays;
import java.util.Scanner;

class convert {


    private String convertS(String s, int numRows) {


        char[] c = s.toCharArray();
        int len = c.length;
        StringBuffer[] sb = new StringBuffer[numRows];
        for (int i = 0; i < sb.length; i++) sb[i] = new StringBuffer();

        int i = 0;
        while (i < len) {
            for (int idx = 0; idx < numRows && i < len; idx++) // vertically down
                sb[idx].append(c[i++]);
            for (int idx = numRows-2; idx >= 1 && i < len; idx--) // obliquely up
                sb[idx].append(c[i++]);
        }
        for (int idx = 1; idx < sb.length; idx++)
            sb[0].append(sb[idx]);
        return sb[0].toString();
        
//        int len = s.length(), j = 0, SemiX = numRows, strO = 0;
//        int sp = len / (numRows+1);
//        int spacecount;
//        int newlen = len + sp*(numRows-1);
//
//        if(numRows <= 0 || s.length() <= numRows)
//            return s;
//
//        String[] strAr = new String[newlen];
//        String[] strOri = s.split("");
//        String result;
//
//        if(numRows % 2 == 0){
//            spacecount = (numRows / 2) - 1;
//        }else
//            spacecount = (numRows / 2);
//
//        for (int i = 0; i < strOri.length; i++) {
//
//            strAr[j] = strOri[i];
//            SemiX--;
//            if (SemiX == 0 && (i + 1) < strOri.length) {
//
//                SemiX = numRows;
//
//                for (int l = 1; l <= spacecount; l++) {
//                    strAr[++j] = "";
//                    SemiX--;
//                }
//
//                strAr[++j] = strOri[++i];
//                SemiX--;
//
//                for (; SemiX > 0; SemiX--) {
//                    strAr[++j] = "";
//                }
//                SemiX = numRows;
//                j++;
//
//            } else {
//                j++;
//            }
//        }
//
//        for (int i = 0; i < numRows; i++) {
//
//            if ((i) == spacecount ) {
//                for (int k = i; k < strAr.length; k = k + numRows) {
//                    strOri[strO] = strAr[k];
//                    strO++;
//                }
//            } else {
//                for (int kx = i; kx < strAr.length; kx = (kx + 2 * numRows)) {
//                    strOri[strO] = strAr[kx];
//                    strO++;
//                }
//            }
//
//        }
//
////        result = Arrays.toString(strOri);
//        result = String.join("",strOri);
//        return result;
    }

    public static void main(String[] arg){
        convert Ob = new convert();

        String s;
        int y;
        Scanner input = new Scanner(System.in);

        while (true) {
            s = input.nextLine();
            y = input.nextInt();

//            numRows = Ob.convertS(numRows,y);
//            numRows = Ob.convertS("PAY",3);
            System.out.println(Ob.convertS(s,y));
//            System.out.println(Ob.convertS("",1));
            input.nextLine();
        }
    }
}

