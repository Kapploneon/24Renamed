/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.LeetCode;

import java.util.Scanner;

class longestSubstring {


    private int lengthOfLongestSubstring(String s) {
        int i_min, i_max, l_min;
        int j;

        String[] stringArray = s.split("");

        i_max = i_min = l_min = 0;

        if(s.equals(""))
            return 0;

        if(stringArray.length > 1) {

            for (int i = 1; i < stringArray.length; i++) {

                for(j = l_min; j < i; j++){

                    if(stringArray[j].equals(stringArray[i])){

                        if((i - l_min) > (i_max - i_min)){
                            i_max = i - 1;
                            i_min = l_min;
                        }

                        l_min = j + 1;
                        break;
                    }
                }

                if( j == i ){
                    if((i - l_min) > (i_max - i_min)){
                        i_max = i;
                        i_min = l_min;
                    }
                }

            }

        }else {
            return stringArray.length;
        }

        return i_max - i_min + 1;

    }

    public static void main(String[] arg){
        longestSubstring Ob = new longestSubstring();

        int x = 0;
        Scanner input = new Scanner(System.in);

        while (input.hasNext()) {
            x = Ob.lengthOfLongestSubstring(input.nextLine());
            System.out.println(x);
        }
    }
}

