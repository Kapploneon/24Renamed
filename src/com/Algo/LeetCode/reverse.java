/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.LeetCode;

import java.util.Scanner;

class reverse {


    private int reverseInteger(int x) {
        int temp, m;
        long y;
        boolean sign;

        if (x >= 0)
            sign = true;
        else {
            sign = false;
            x = x * (-1);
        }

        m = 10;
        y = 0;

        while ( x > 0 ){
            temp = x % m;

            x = x - temp;
            x = x / m ;

            y = ((m * y) + (temp));

            if( y > Integer.MAX_VALUE)
            {
                y = 0;
                break;
            }
        }

        if(sign)
            return (int) y;
        else
            return (int) (y*(-1));

    }

    public static void main(String[] arg){
        reverse Ob = new reverse();

        int x;
        Scanner input = new Scanner(System.in);

        while (input.hasNext()) {
            x = Ob.reverseInteger(input.nextInt());
//            x = Ob.reverseInteger(2147483647);
            System.out.println(x);
        }
    }
}

